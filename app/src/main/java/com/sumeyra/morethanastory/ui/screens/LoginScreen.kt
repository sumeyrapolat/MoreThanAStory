package com.sumeyra.morethanastory.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sumeyra.morethanastory.data.preferences.PreferencesManager
import com.sumeyra.morethanastory.ui.theme.DarkerPastelPink
import com.sumeyra.morethanastory.ui.theme.LightBlue
import com.sumeyra.morethanastory.ui.theme.LightPurple
import com.sumeyra.morethanastory.ui.theme.MediumBlue
import com.sumeyra.morethanastory.ui.theme.MediumPurple
import com.sumeyra.morethanastory.ui.theme.PastelPink
import com.sumeyra.morethanastory.ui.theme.PastelYellow
import com.sumeyra.morethanastory.ui.theme.Pink
import com.sumeyra.morethanastory.viewmodel.AuthViewModel
import com.sumeyra.morethanastory.viewmodel.states.LoginState
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    preferencesManager: PreferencesManager = PreferencesManager(LocalContext.current)
) {
    val context = LocalContext.current
    val email = remember { mutableStateOf(preferencesManager.getEmail() ?: "") }
    val password = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val showResetPasswordDialog = remember { mutableStateOf(false) }
    val rememberMeChecked = remember { mutableStateOf(preferencesManager.isRememberMeChecked()) }
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val loginState = authViewModel.loginState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val displayMetrics = context.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels.toFloat()
    val screenHeight = displayMetrics.heightPixels.toFloat()


    val backgroundGradient = Brush.linearGradient(
        colors = listOf(
            LightPurple,
            PastelPink,
            MediumBlue,
            PastelPink,
            LightBlue
        ),
        start = Offset(0f, 0f),
        end = Offset(0f, screenHeight),
        tileMode = TileMode.Clamp
    )



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Login",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Pink,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = email.value,
                        onValueChange = { email.value = it },
                        label = { Text("Email", fontSize = 16.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(emailFocusRequester),
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() })
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    OutlinedTextField(
                        value = password.value,
                        onValueChange = { password.value = it },
                        label = { Text("Password", fontSize = 16.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(passwordFocusRequester),
                        shape = RoundedCornerShape(8.dp),
                        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible.value) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, end = 8.dp, top = 8.dp)
                    ) {
                        Checkbox(
                            checked = rememberMeChecked.value,
                            onCheckedChange = { rememberMeChecked.value = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MediumBlue,
                                uncheckedColor = Color.Gray,
                                checkmarkColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Remember Me",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        onClick = {
                            if (password.value.length < 8) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Please enter a valid password!",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            } else {
                                if (rememberMeChecked.value) {
                                    preferencesManager.saveLoginData(email.value, true)
                                } else {
                                    preferencesManager.clearLoginData()
                                }
                                authViewModel.login(email.value, password.value, onSuccess = {})
                            }
                        },
                        shape = RoundedCornerShape(45.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.linearGradient(listOf(
                                MediumBlue.copy(alpha = 0.8f),
                                DarkerPastelPink,
                                MediumBlue.copy(alpha = 0.8f)
                            )), shape = RoundedCornerShape(45.dp))
                    ) {
                        Text(text = "Login", modifier = Modifier.padding(8.dp), fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    TextButton(onClick = { navController.navigate("signup") }) {
                        Text(text = "Don't have an account? Sign Up", fontSize = 16.sp, color = Pink)
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    TextButton(onClick = { showResetPasswordDialog.value = true }) {
                        Text(text = "Forgot Password", fontSize = 16.sp, color = Pink)
                    }
                }
            }
            if (showResetPasswordDialog.value) {
                AlertDialog(
                    onDismissRequest = { showResetPasswordDialog.value = false },
                    title = {
                        Text("Reset Password", color = Pink, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    },
                    text = {
                        Column {
                            Text(
                                "Please enter your email to receive password reset instructions.",
                                color = Color.DarkGray,
                                fontSize = 16.sp
                            )
                            OutlinedTextField(
                                value = email.value,
                                onValueChange = { email.value = it },
                                label = { Text("Email", color = Color.Gray) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                authViewModel.forgotPassword(email.value)
                                showResetPasswordDialog.value = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MediumPurple, contentColor = Color.White)
                        ) {
                            Text("Send")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showResetPasswordDialog.value = false }) {
                            Text("Cancel", color = Pink)
                        }
                    },
                    containerColor = Color.White.copy(0.9f),
                    shape = RoundedCornerShape(25.dp)
                )
            }
            LaunchedEffect(key1 = loginState.value) {
                when (loginState.value) {
                    is LoginState.Loading -> {
                        // Optionally handle loading state
                    }
                    is LoginState.Success -> {
                        authViewModel.loadUserData()
                        navController.navigate("feed")
                    }
                    is LoginState.Error -> {
                        snackbarHostState.showSnackbar(
                            message = (loginState.value as LoginState.Error).error,
                            duration = SnackbarDuration.Short
                        )
                    }
                    else -> {
                        Log.d("SignInScreen", "Unknown state: ${loginState.value}")
                    }
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            snackbar = { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
