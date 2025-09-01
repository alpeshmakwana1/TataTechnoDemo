package com.pmirkelam.tatatechnodemo.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pmirkelam.tatatechnodemo.viewmodel.RandomTextViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RandomTextScreen(viewModel: RandomTextViewModel) {
    val texts = viewModel.texts.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.fetch() }) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(texts.value) { text ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(Modifier.padding(8.dp)) {
                        Text(text.value, style = MaterialTheme.typography.titleMedium)
                        Text("Length: ${text.length}")
                        Text("Created: ${text.created}")
                    }
                }
            }
        }
    }
}