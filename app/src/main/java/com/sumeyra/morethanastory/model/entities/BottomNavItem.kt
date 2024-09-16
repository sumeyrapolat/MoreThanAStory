package com.sumeyra.morethanastory.model.entities

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit
)