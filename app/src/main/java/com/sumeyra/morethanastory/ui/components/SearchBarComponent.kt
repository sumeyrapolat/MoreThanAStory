package com.sumeyra.morethanastory.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sumeyra.morethanastory.ui.theme.MediumPurple
import com.sumeyra.morethanastory.ui.theme.SoftBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarComponent(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search..."
) {

    TextField(
        value = query,
        onValueChange = { onQueryChange(it) },
        modifier = modifier
            .fillMaxWidth()
            .padding(start=12.dp, end=12.dp, top=8.dp, bottom=8.dp),
        placeholder = { androidx.compose.material.Text(text = placeholder, fontSize = 16.sp) },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = androidx.compose.material.TextFieldDefaults.textFieldColors(
            backgroundColor = SoftBlue.copy(alpha = 0.2f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            textColor = Color.Black
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onSearch() }
        ),
        trailingIcon = {
            androidx.compose.material.IconButton(onClick = { onSearch() }) {
                androidx.compose.material.Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            }
        }
    )
}

