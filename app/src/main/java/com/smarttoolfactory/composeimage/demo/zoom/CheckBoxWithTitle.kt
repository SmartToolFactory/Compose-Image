package com.smarttoolfactory.composeimage.demo.zoom

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun CheckBoxWithTitle(
    label: String,
    state: Boolean,
    onStateChange: (Boolean) -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(40.dp)
        .clickable(
            role = Role.Checkbox,
            onClick = {
                onStateChange(!state)
            }
        )
        .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = state,
            onCheckedChange = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label)
    }
}