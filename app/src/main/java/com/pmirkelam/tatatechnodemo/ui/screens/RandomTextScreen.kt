import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
    val isLoading by viewModel.loading.collectAsState()
    val listState = rememberLazyListState()

    // Observe errors
    LaunchedEffect(Unit) {
        viewModel.error.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(texts) {
        listState.scrollToItem(0)
    }

    // Close dialog automatically when loading finishes successfully
    LaunchedEffect(isLoading) {
        if (!isLoading && showDialog && inputLength.isNotEmpty()) {
            showDialog = false
            inputLength = ""
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(title = {Text("Tata Technologies Demo")}
                )
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
                RandomTextList(texts = texts, viewModel = viewModel, listState)
            }
        }
    }

    InputLengthDialog(
        showDialog = showDialog,
        inputLength = inputLength,
        onDismiss = { showDialog = false },
        onValueChange = { if (!isLoading) inputLength = it },
        onGenerate = {
            inputLength.toIntOrNull()?.let { len ->
                viewModel.generateRandom(len)
            } ?: Toast.makeText(context, "Invalid length", Toast.LENGTH_SHORT).show()
        },
        isLoading = isLoading
    )
}

/**
 * List of Strings generated using content providers
 * @param texts list of strings fetched from Room Database
 * @param viewModel viewmodel object to manage the list of strings
 * @param listState scroll the list to first position on adding new string
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RandomTextList(
    texts: List<RandomText>,
    viewModel: RandomTextViewModel,
    listState: LazyListState
) {
    val scope = rememberCoroutineScope()

    LazyColumn(Modifier.fillMaxWidth(), state = listState) {
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

/**
 * List Item ViewHolders
 * */
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

/**
 * Dialog for entering the desired length of the random string.
 *
 * Shows a text input field and buttons to generate or cancel.
 * Displays a loading indicator while the generation is in progress.
 *
 * @param showDialog Whether the dialog should be visible.
 * @param inputLength Current input value for length.
 * @param onDismiss Called when the dialog is dismissed.
 * @param onValueChange Called when the input value changes.
 * @param onGenerate Called when the generate button is clicked.
 * @param isLoading Whether a generation operation is in progress.
 */

@Composable
private fun InputLengthDialog(
    showDialog: Boolean,
    inputLength: String,
    onDismiss: () -> Unit,
    onValueChange: (String) -> Unit,
    onGenerate: () -> Unit,
    isLoading: Boolean
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { if (!isLoading) onDismiss() },
            confirmButton = {
                TextButton(
                    onClick = { if (!isLoading) onGenerate() },
                    enabled = !isLoading
                ) {
                    Text("Generate")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { if (!isLoading) onDismiss() },
                    enabled = !isLoading
                ) { Text("Cancel") }
            },
            title = { Text("Enter String Length") },
            text = {
                Column {
                    OutlinedTextField(
                        value = inputLength,
                        onValueChange = onValueChange,
                        label = { Text("Length") },
                        enabled = !isLoading
                    )
                    if (isLoading) {
                        Spacer(Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Generating...", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        )
    }
}