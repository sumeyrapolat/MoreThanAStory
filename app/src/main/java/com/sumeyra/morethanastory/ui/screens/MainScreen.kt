package com.sumeyra.morethanastory.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sumeyra.morethanastory.model.entities.BottomNavItem
import com.sumeyra.morethanastory.model.repository.AuthRepository
import com.sumeyra.morethanastory.ui.components.BottomBar
import com.sumeyra.morethanastory.ui.navigation.Router
import com.sumeyra.morethanastory.ui.theme.Pink

@ExperimentalMaterial3Api
@Composable
fun MainScreen(navController: NavHostController, authRepository: AuthRepository) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination?.route

    val showMenu = remember { mutableStateOf(false) } // Menü için durum

    // Define the Bottom Navigation items (Home, Favorites, and Category)
    val bottomNavItems = listOf(
        BottomNavItem(
            route = "feed",
            icon = Icons.Filled.Home,
            label = "Home",
            onClick = { navController.navigate("feed") }
        ),
        BottomNavItem(
            route = "category",
            icon = Icons.Filled.TravelExplore,
            label = "Category",
            onClick = { navController.navigate("category") }
        ),
        BottomNavItem(
            route = "favorites",
            icon = Icons.Filled.Bookmarks,
            label = "Saved",
            onClick = { navController.navigate("favorites") }
        )
    )

    Scaffold(
        topBar = {
            if (currentDestination == "feed" || currentDestination == "favorites" || currentDestination == "category") {
                TopAppBar(
                    title = { Text("") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White, // Background color
                        titleContentColor = Color.Black // Title text color
                    ),
                    actions = {
                        IconButton(onClick = { showMenu.value = true }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu",
                                modifier = Modifier.size(26.dp))
                        }

                        DropdownMenu(
                            expanded = showMenu.value,
                            onDismissRequest = { showMenu.value = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                    ) {
                                        Text("Sign Out", color = Pink, fontWeight = FontWeight.SemiBold, fontSize = 20.sp) // Text rengi ayarlanıyor
                                    }
                                },
                                onClick = {
                                    authRepository.signOut()
                                    navController.navigate("login") {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = true
                                        }
                                    }
                                    showMenu.value = false
                                }
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (currentDestination == "feed" || currentDestination == "favorites" || currentDestination == "category") {
                BottomBar(navController = navController, bottomNavItems = bottomNavItems, onItemClick = { navController.navigate(it) })
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                Router(navController = navController, authRepository = authRepository)
            }
        }
    )
}
