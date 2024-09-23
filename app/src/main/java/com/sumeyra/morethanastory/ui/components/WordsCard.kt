package com.sumeyra.morethanastory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sumeyra.morethanastory.ui.theme.PastelPink
import com.sumeyra.morethanastory.ui.theme.backCardGradientColor
import com.sumeyra.morethanastory.ui.theme.wordCardGradientColor

@Composable
fun WordsCard(usedWords: List<String>, onAddClick: () -> Unit) {

    Box(
        modifier = Modifier
            .padding(8.dp)
            .width(160.dp) // Sabit genişlik
            .height(250.dp) // Sabit yükseklik
    ) {
        // Arkadaki gradient Card
        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = 8.dp, y = 8.dp), // Hafif bir kaydırma ekleyin
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backCardGradientColor)
            )
        }

        // Üstteki beyaz Card
        Card(
            modifier = Modifier
                .matchParentSize()
                .border(2.dp, wordCardGradientColor , shape = RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(usedWords) { word ->
                        Text(
                            text = word,
                            color = Color.Black,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .size(35.dp)
                        .align(Alignment.BottomStart) // Sol alt köşeye hizalama
                        .background(PastelPink, shape = CircleShape)
                        .padding(bottom = 2.dp, start = 2.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Story",
                        tint = Color.Black,
                    )
                }
            }
        }
    }
}
