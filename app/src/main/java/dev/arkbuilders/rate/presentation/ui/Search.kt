package dev.arkbuilders.rate.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.presentation.theme.ArkColor

@Preview(showBackground = true)
@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    text: String = "",
    placeHolderText: String = "Search",
    onValueChange: (String) -> Unit = {},
) {
    OutlinedTextField(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        value = text,
        onValueChange = { onValueChange(it) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "",
                tint = ArkColor.FGQuarterary
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults
            .colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedBorderColor = ArkColor.Border,
                unfocusedBorderColor = ArkColor.Border
            ),
        placeholder = {
            Text(
                text = placeHolderText,
                color = ArkColor.TextPlaceHolder,
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SearchTextFieldWithSort(
    modifier: Modifier = Modifier,
    text: String = "",
    placeHolderText: String = "Search",
    onValueChange: (String) -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f),
            value = text,
            onValueChange = { onValueChange(it) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "",
                    tint = ArkColor.FGQuarterary
                )
            },
            shape = RoundedCornerShape(8.dp),
            placeholder = {
                Text(
                    text = placeHolderText,
                    color = ArkColor.TextPlaceHolder,
                )
            },
            colors = OutlinedTextFieldDefaults
                .colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedBorderColor = ArkColor.Border,
                    unfocusedBorderColor = ArkColor.Border
                ),
        )
        IconButton(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .border(
                    width = 1.dp,
                    ArkColor.Border,
                    RoundedCornerShape(8.dp)
                ),
            onClick = {

            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_sort),
                contentDescription = "",
                tint = ArkColor.FGSecondary
            )
        }
    }
}