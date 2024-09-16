package com.sumeyra.morethanastory.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.sumeyra.morethanastory.model.entities.User
import com.sumeyra.morethanastory.model.repository.AuthRepository
import com.sumeyra.morethanastory.viewmodel.states.LoginState
import com.sumeyra.morethanastory.viewmodel.states.PasswordResetState
import com.sumeyra.morethanastory.viewmodel.states.ProfilePhotoState
import com.sumeyra.morethanastory.viewmodel.states.SignUpState
import com.sumeyra.morethanastory.viewmodel.states.UserDataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    ): ViewModel() {

    private val _passwordResetState = MutableStateFlow<PasswordResetState>(PasswordResetState.Idle)
    val passwordResetState: StateFlow<PasswordResetState> = _passwordResetState

    private val _userDataState = MutableStateFlow<UserDataState>(UserDataState.Loading)
    val userDataState: StateFlow<UserDataState> = _userDataState

    private val _userLoggedInState = MutableStateFlow<Boolean>(false)
    val userLoggedInState: StateFlow<Boolean> = _userLoggedInState

    private val _profilePhotoState = MutableStateFlow<ProfilePhotoState>(ProfilePhotoState.Idle)
    val profilePhotoState: StateFlow<ProfilePhotoState> = _profilePhotoState

    private val _userProfilePhotoUri = MutableStateFlow<Uri?>(null)
    val userProfilePhotoUri: StateFlow<Uri?> = _userProfilePhotoUri

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState = _signUpState.asStateFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()


    init {
        checkUserLoggedIn()
        if (_userLoggedInState.value) {
            loadCachedUserDataIfNeeded()
        }
    }

    private fun checkUserLoggedIn() {
        _userLoggedInState.value = authRepository.checkUserLoggedIn()
    }

    fun onViewInitialized() {
        if (_userDataState.value !is UserDataState.Success) {
            loadUserData()
        }
    }

    fun loadCachedUserDataIfNeeded() {
        val cachedData = authRepository.getCachedUserData()
        if (cachedData != null) {
            _userDataState.value = UserDataState.Success(
                firstName = cachedData.firstName,
                lastName = cachedData.lastName,
                email = cachedData.email
            )
            _userProfilePhotoUri.value = authRepository.getProfilePhotoUrl()?.let { Uri.parse(it) }
            Log.d("AuthViewModel", "Cached data loaded: $cachedData")
        } else {
            loadUserData()
        }
    }

    fun loadUserData() {
        val userID = authRepository.getCurrentUserID()
        if (userID != null) {
            viewModelScope.launch {
                val result = authRepository.loadUserData(userID)
                if (result.isSuccess) {
                    val userData = result.getOrNull()
                    Log.d("AuthViewModel", "LoadUserData result: $userData")
                    if (userData != null) {
                        _userDataState.value = UserDataState.Success(
                            firstName = userData.firstName,
                            lastName = userData.lastName,
                            email = userData.email
                        )
                        _userProfilePhotoUri.value = authRepository.getProfilePhotoUrl()?.let { Uri.parse(it) }
                    } else {
                        _userDataState.value = UserDataState.Error("User data is null")
                    }
                } else {
                    val errorMessage = when (result.exceptionOrNull()) {
                        is FirebaseAuthInvalidCredentialsException -> "Invalid credentials"
                        is FirebaseAuthInvalidUserException -> "User not found"
                        else -> "Unknown error"
                    }
                    _userDataState.value = UserDataState.Error(errorMessage)
                }
            }
        } else {
            _userDataState.value = UserDataState.Error("User not logged in")
        }
    }

    fun sendPasswordResetEmail(email: String) {
        _passwordResetState.value = PasswordResetState.Loading
        authRepository.sendPasswordResetEmail(
            email = email,
            onSuccess = {
                _passwordResetState.value = PasswordResetState.Success("Password reset email sent")
            },
            onError = { errorMessage ->
                _passwordResetState.value = PasswordResetState.Error(errorMessage)
            }
        )
    }

    fun signOut() {
        authRepository.signOut()
        _userDataState.value = UserDataState.Error("User not logged in")
        _userLoggedInState.value = false
    }


    fun resetPasswordResetState() {
        _passwordResetState.value = PasswordResetState.Idle
    }

    fun forgotPassword(email: String) {
        sendPasswordResetEmail(email)
    }

    fun updateUserProfilePhoto(imageUri: Uri) {
        val userID = authRepository.getCurrentUserID()
        if (userID != null) {
            viewModelScope.launch {
                _profilePhotoState.value = ProfilePhotoState.Loading
                val result = authRepository.uploadProfilePhoto(userID, imageUri)
                if (result.isSuccess) {
                    _profilePhotoState.value = ProfilePhotoState.Success(result.getOrNull()!!)
                    _userProfilePhotoUri.value = imageUri
                } else {
                    _profilePhotoState.value = ProfilePhotoState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        } else {
            _profilePhotoState.value = ProfilePhotoState.Error("No user is currently logged in.")
        }
    }

    fun signUp(email: String, password: String, firstName: String, lastName: String) {
        _signUpState.value = SignUpState.Loading
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userID = auth.currentUser?.uid
                        val userEmail = auth.currentUser?.email
                        if (userID != null) {
                            saveUserToFirestore(userID, firstName, lastName, userEmail ?: email)
                        } else {
                            _signUpState.value = SignUpState.Error("User email not found")
                        }
                    } else {
                        _signUpState.value = SignUpState.Error(task.exception?.message ?: "Unknown error")
                    }
                }
        }
    }

    private fun saveUserToFirestore(userID: String, firstName: String, lastName: String, email: String) {
        val signUpUser = User(firstName = firstName, lastName = lastName, email = email)

        db.collection("Users").document(userID).set(signUpUser)
            .addOnSuccessListener {
                _signUpState.value = SignUpState.Success("Sign up successful!")
            }
            .addOnFailureListener { e ->
                _signUpState.value = SignUpState.Error("Failed to save user to Firestore: ${e.message}")
            }
    }

    fun resetSignUpState() {
        _signUpState.value = SignUpState.Idle
    }


    fun login(email: String, password: String, onSuccess: () -> Unit) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            val result = authRepository.signIn(email, password)
            if (result.isSuccess) {
                onSuccess()
                authRepository.loadUserData(auth.currentUser!!.uid    )
                _loginState.value = LoginState.Success("Sign in successful!")
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                _loginState.value = LoginState.Error(errorMessage)
            }
        }
    }

    fun resetSignInState() {
        _loginState.value = LoginState.Idle
    }
}