package com.sumeyra.morethanastory.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.opencsv.CSVReader
import com.sumeyra.morethanastory.data.api.APIService
import com.sumeyra.morethanastory.data.db.dao.LikedStoryDao
import com.sumeyra.morethanastory.data.db.entitiy.LikedStoryEntity
import com.sumeyra.morethanastory.data.db.entitiy.WordEntity
import com.sumeyra.morethanastory.model.entities.Post
import com.sumeyra.morethanastory.model.entities.Story
import com.sumeyra.morethanastory.model.repository.PostsRepository
import com.sumeyra.morethanastory.model.repository.WordsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.StringReader
import javax.inject.Inject


@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: WordsRepository,
    private val postsRepository: PostsRepository,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val likedStoryDao: LikedStoryDao,
    private val apiService: APIService, // APIService'i Hilt ile enjekte edin

) : ViewModel() {

    private val _words = MutableStateFlow<List<List<String>>>(emptyList())
    val words: StateFlow<List<List<String>>> = _words

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories

    private val _postState = MutableStateFlow<PostState>(PostState.Idle)
    val postState: StateFlow<PostState> = _postState

    private val _likedStories = MutableStateFlow<Set<String>>(emptySet())
    val likedStories: StateFlow<Set<String>> = _likedStories

    init {
        fetchStoriesFromFirestore()
        loadLikedStories() // Beğenilen hikayeleri ilk başta yükle
    }

    private fun loadLikedStories() {
        viewModelScope.launch {
            val likedStoryEntities = likedStoryDao.getAllLikedStories()
            _likedStories.value = likedStoryEntities.map { it.storyId }.toSet()
        }
    }

    fun toggleLike(storyId: String) {
        viewModelScope.launch {
            val newSet = _likedStories.value.toMutableSet()
            if (newSet.contains(storyId)) {
                newSet.remove(storyId)
                removeLikedStory(storyId)
            } else {
                newSet.add(storyId)
                saveLikedStory(storyId)
            }
            _likedStories.value = newSet
        }
    }

    fun fetchStoriesFromFirestore() {
        viewModelScope.launch {
            _postState.value = PostState.Loading // Verileri yüklemeye başladığımızda Loading durumuna geçiyoruz

            val postsResult = postsRepository.getAllPosts()
            if (postsResult.isSuccess) {
                val posts = postsResult.getOrNull().orEmpty()

                // Tüm kullanıcı profillerini asenkron olarak çekeriz.
                val stories = posts.map { post ->
                    async {
                        val userProfileResult = postsRepository.getUserProfile(post.userId)
                        userProfileResult.getOrNull()?.let { userProfile ->
                            Story(
                                userPhoto = userProfile.profilePhotoUrl ?: "",
                                userName = "${userProfile.firstName} ${userProfile.lastName}",
                                title = post.title,
                                content = post.content,
                                usedWords = post.usedWords,
                                timestamp = post.timestamp,
                                userEmail = userProfile.email,
                                storyId = post.postId
                            )
                        }
                    }
                }.awaitAll().filterNotNull() // Null olanları filtrele

                _stories.value = stories
                _postState.value = PostState.Success // Veri yüklendiğinde Success durumuna geçiyoruz
            } else {
                _postState.value = PostState.Error("Error fetching stories")
            }
        }
    }

    fun fetchWords() {
        viewModelScope.launch {
            try {
                if (repository.getAllWords().isEmpty()) {
                    Log.d("FeedViewModel", "Fetching words from API")
                    val csvData = apiService.getWordsCSV() // Artık doğrudan apiService'i kullanıyoruz
                    val csvReader = CSVReader(StringReader(csvData))

                    csvReader.readNext() // Başlık satırını atla

                    val wordLists = csvReader.readAll().map { it.toList() }
                    val groupedWordLists: List<List<String>> = wordLists.chunked(size = 12).map { it.flatten() }

                    groupedWordLists.forEach { group ->
                        val wordEntities = group.map { WordEntity(word = it) }
                        repository.insertWords(wordEntities)
                    }

                    _words.value = groupedWordLists

                } else {
                    Log.d("FeedViewModel", "Fetching words from database")
                    val wordsFromDb = repository.getAllWords()
                    val groupedWords = wordsFromDb.map { it.word }.chunked(12)
                    _words.value = groupedWords
                }
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Error fetching words: ${e.message}")
            }
        }
    }

    fun savePost(title: String, content: String, usedWords: List<String>) {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            _postState.value = PostState.Error("User not logged in")
            return
        }

        val post = Post(
            userId = currentUser.uid,
            title = title,
            content = content,
            usedWords = usedWords
        )

        _postState.value = PostState.Loading

        viewModelScope.launch {
            val result = postsRepository.addPost(post, currentUser.uid)
            if (result.isSuccess) {
                _postState.value = PostState.Success
            } else {
                _postState.value = PostState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun resetState() {
        _postState.value = PostState.Idle
    }

    fun saveLikedStory(storyId: String) {
        viewModelScope.launch {
            likedStoryDao.insertLikedStory(LikedStoryEntity(storyId))
        }
    }

    fun removeLikedStory(storyId: String) {
        viewModelScope.launch {
            likedStoryDao.deleteLikedStory(storyId)
        }
    }

    suspend fun isStoryLiked(storyId: String): Boolean {
        return likedStoryDao.isStoryLiked(storyId) != null
    }

    sealed class PostState {
        object Idle : PostState()
        object Loading : PostState()
        object Success : PostState()
        data class Error(val errorMessage: String) : PostState()
    }

}