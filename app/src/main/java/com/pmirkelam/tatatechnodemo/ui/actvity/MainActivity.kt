package com.pmirkelam.tatatechnodemo.ui.actvity

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pmirkelam.tatatechnodemo.data.local.AppDatabaseProvider
import com.pmirkelam.tatatechnodemo.data.providers.IavAppDataProvider
import com.pmirkelam.tatatechnodemo.data.repo.RandomTextRepository
import com.pmirkelam.tatatechnodemo.ui.screens.RandomTextScreen
import com.pmirkelam.tatatechnodemo.ui.theme.TataTechnoDemoTheme
import com.pmirkelam.tatatechnodemo.viewmodel.RandomTextViewModel
import com.pmirkelam.tatatechnodemo.viewmodel.RandomTextViewModelFactory

class MainActivity : ComponentActivity() {
    private val viewModel: RandomTextViewModel by viewModels {
        val dao = AppDatabaseProvider.getDatabase(applicationContext).randomTextDao()
        val remote = IavAppDataProvider(applicationContext)
        val repo = RandomTextRepository(remote,dao)
        RandomTextViewModelFactory(repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TataTechnoDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppUI(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                                viewModel
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppUI(name: String, modifier: Modifier = Modifier, viewModel: RandomTextViewModel) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )

    RandomTextScreen(viewModel)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TataTechnoDemoTheme {
//        Greeting("Android")
    }
}