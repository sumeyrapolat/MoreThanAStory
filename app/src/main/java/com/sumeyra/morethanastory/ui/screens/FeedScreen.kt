package com.sumeyra.morethanastory.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sumeyra.morethanastory.ui.components.CategoryTabs
import com.sumeyra.morethanastory.ui.components.SearchBarComponent
import com.sumeyra.morethanastory.ui.components.StoryCard
import com.sumeyra.morethanastory.ui.components.WordsCard
import com.sumeyra.morethanastory.viewmodel.AuthViewModel
import com.sumeyra.morethanastory.viewmodel.FeedViewModel
import java.util.UUID



@Composable
fun FeedScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    viewModel: FeedViewModel = hiltViewModel()
) {
    val words by viewModel.words.collectAsState()
    val stories by viewModel.stories.collectAsState()
    val postState by viewModel.postState.collectAsState()
    val likedStories by viewModel.likedStories.collectAsState()

    var selectedCategory by remember { mutableStateOf("Stories") }
    val userLoggedIn by authViewModel.userLoggedInState.collectAsState()

    var searchText = remember { mutableStateOf("") }
    val filteredStories = remember(stories) { mutableStateOf(stories) }

    var query by remember { mutableStateOf("") }


    // Arama işlevi
    fun performSearch() {
        val query = searchText.value.lowercase().trim()
        filteredStories.value = if (query.isEmpty()) {
            stories
        } else {
            stories.filter { story ->
                story.usedWords.any { word ->
                    word.lowercase().contains(query)
                }
            }
        }
    }

    LaunchedEffect(stories) {
        performSearch()
    }

    LaunchedEffect(userLoggedIn) {
        if (userLoggedIn) {
            viewModel.fetchStoriesFromFirestore()
            viewModel.fetchWords()
        } else {
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }
    }

    if (userLoggedIn) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (selectedCategory == "Stories") {
                SearchBarComponent(
                    query = searchText.value,
                    onQueryChange = { searchText.value = it },
                    placeholder = "Search...",
                    onSearch = {
                        performSearch()
                        Log.d("FeedScreen", "Searching for: ${searchText.value}")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp, horizontal = 5.dp)
                )
            }
            CategoryTabs(onCategorySelected = { category ->
                selectedCategory = category
                Log.d("FeedScreen", "Seçilen kategori: $category")
            })

            when (postState) {
                is FeedViewModel.PostState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is FeedViewModel.PostState.Success -> {
                    when (selectedCategory) {
                        "Stories" -> {
                            if (filteredStories.value.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Henüz bir hikaye eklenmedi.",
                                        color = Color.Gray,
                                        fontSize = 18.sp
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(
                                        items = filteredStories.value,
                                        key = { story ->
                                            if (story.storyId.isNullOrEmpty()) {
                                                UUID.randomUUID().toString() // Boş veya null storyId için UUID kullan
                                            } else {
                                                story.storyId
                                            }
                                        }
                                    ) { story ->
                                        val isLiked = likedStories.contains(story.storyId)
                                        StoryCard(
                                            userPhoto = story.userPhoto,
                                            userName = story.userName,
                                            storyTitle = story.title,
                                            storyContent = story.content,
                                            usedWords = story.usedWords,
                                            storyId = story.storyId
                                        )
                                    }
                                }
                            }
                        }
                        "Words" -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(words) { wordsGroup ->
                                    WordsCard(usedWords = wordsGroup, onAddClick = {
                                        navController.navigate("addstory/${wordsGroup.joinToString(",")}")
                                    })
                                }
                            }
                        }
                    }
                }
                is FeedViewModel.PostState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Bir hata oluştu: ${(postState as FeedViewModel.PostState.Error).errorMessage}",
                            color = Color.Red,
                            fontSize = 18.sp
                        )
                    }
                }
                else -> {
                    // Başka durumlar için gerekli işlemleri burada tanımlayabilirsiniz
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}