import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pmirkelam.tatatechnodemo.data.model.RandomText
import com.pmirkelam.tatatechnodemo.viewmodel.RandomTextViewModel
import kotlinx.coroutines.launch
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBox
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomTextScreen(viewModel: RandomTextViewModel = hiltViewModel()) {
    val texts by viewModel.allTexts.collectAsState(initial = emptyList())
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var inputLength by remember { mutableStateOf("") }

    // Observe errors
    LaunchedEffect(Unit) {
        viewModel.error.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Random Text Generator") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        bottomBar = {
            Button(
                onClick = { viewModel.deleteAll() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Delete All", color = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            RandomTextList(texts = texts, viewModel = viewModel)
        }
    }

    InputLengthDialog(
        showDialog = showDialog,
        inputLength = inputLength,
        onDismiss = { showDialog = false },
        onValueChange = { inputLength = it },
        onGenerate = {
            inputLength.toIntOrNull()?.let { len ->
                viewModel.generateRandom(len)
                inputLength = ""
                showDialog = false
            } ?: Toast.makeText(context, "Invalid length", Toast.LENGTH_SHORT).show()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RandomTextList(texts: List<RandomText>, viewModel: RandomTextViewModel) {
    val scope = rememberCoroutineScope()

    LazyColumn(Modifier.fillMaxWidth()) {
        items(texts, key = { it.created }) { item ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = {
                    if (it == SwipeToDismissBoxValue.EndToStart) {
                        scope.launch { viewModel.delete(item) }
                        true
                    } else false
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    when (dismissState.dismissDirection) {
                        SwipeToDismissBoxValue.EndToStart -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Red)
                                    .padding(16.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.White
                                )
                            }
                        }

                        else -> {}
                    }
                },
                content = {
                    RandomTextListItem(item = item)
                }
            )
        }
    }
}

@Composable
private fun RandomTextListItem(item: RandomText) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("Value: ${item.value}")
            Text("Length: ${item.length}")
            Text("Created: ${item.created}")
        }
    }
}

@Composable
private fun InputLengthDialog(
    showDialog: Boolean,
    inputLength: String,
    onDismiss: () -> Unit,
    onValueChange: (String) -> Unit,
    onGenerate: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = { TextButton(onClick = onGenerate) { Text("Generate") } },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
            title = { Text("Enter String Length") },
            text = {
                OutlinedTextField(
                    value = inputLength,
                    onValueChange = onValueChange,
                    label = { Text("Length") }
                )
            }
        )
    }
}
