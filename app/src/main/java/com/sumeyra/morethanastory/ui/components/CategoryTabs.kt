package com.sumeyra.morethanastory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sumeyra.morethanastory.ui.theme.MediumPurple
import com.sumeyra.morethanastory.ui.theme.categoryGradientColor

@Composable
fun CategoryTabs(onCategorySelected: (String) -> Unit) {
    val categories = listOf("Stories", "Words")
    var selectedCategory by remember { mutableStateOf(0) }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White) // Kart arka planına uygun bir arka plan rengi
    ) {

        TabRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White), // Arka plan rengi
            selectedTabIndex = selectedCategory,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedCategory]),
                    color = MediumPurple // Seçili kategori için gösterge rengi
                )
            }
        ) {
            categories.forEachIndexed { index, category ->
                Tab(
                    text = {
                        Text(
                            text = category,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (selectedCategory == index) Color.White else Color.Gray,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    selected = selectedCategory == index,
                    onClick = {
                        selectedCategory = index
                        onCategorySelected(category)
                    },
                    modifier = Modifier
                        .padding(vertical = 5.dp, horizontal = 5.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .then(
                            if (selectedCategory == index) {
                                Modifier.background(categoryGradientColor) // Gradient arka plan
                            } else {
                                Modifier.background(Color.Transparent) // Seçili değilse transparan
                            }
                        )
                )

            }
        }
    }
}
