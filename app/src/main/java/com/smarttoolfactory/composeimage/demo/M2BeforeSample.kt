package com.smarttoolfactory.composeimage.demo

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.composeimage.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun M2BeforeSample() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .border(3.dp, Color(0xff9C27B0), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {

        Spacer(modifier = Modifier.height(40.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { /*TODO*/ }) {
            Text("Button")
        }
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { /*TODO*/ }) {
            Text("OutlinedButton")
        }
        Spacer(modifier = Modifier.height(10.dp))

        Card {
            Column {
                Image(
                    painter = painterResource(id = R.drawable.landscape5_before),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "Image inside Card",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        Row {
            var checked by remember { mutableStateOf(true) }
            Spacer(modifier = Modifier.width(10.dp))
            Switch(checked = checked, onCheckedChange = { checked = it })
            Spacer(modifier = Modifier.width(10.dp))
            Checkbox(checked = checked, onCheckedChange = { checked = it })
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = null)
            }
            Spacer(modifier = Modifier.width(10.dp))
            ExtendedFloatingActionButton(text = {
                Text(text = "Extended FAB")
            }, onClick = { /*TODO*/ })
        }

        Chip(onClick = { /*TODO*/ }) {
            Text("Chip")
        }
    }
}