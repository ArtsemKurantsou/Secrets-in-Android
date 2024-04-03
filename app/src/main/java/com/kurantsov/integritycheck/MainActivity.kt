package com.kurantsov.integritycheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kurantsov.integritycheck.ui.MainViewModel
import com.kurantsov.integritycheck.ui.theme.IntegrityCheckPoCTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class MainActivity : ComponentActivity() {
    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntegrityCheckPoCTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val state by viewModel.state.collectAsState()
                    Box(
                        contentAlignment = Alignment.Center, modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        MainContent(state = state, onLoad = viewModel::onLoad)
                    }
                }
            }
        }
    }
}

@Composable
private fun MainContent(
    state: MainViewModel.State,
    onLoad: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is MainViewModel.State.Error -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Error loading secrets, error message - ${state.message}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Button(onClick = onLoad, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Retry")
                }
            }
        }

        MainViewModel.State.Loading -> {
            CircularProgressIndicator(modifier = Modifier.size(64.dp))
        }

        MainViewModel.State.NotLoaded -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Secrets not loaded",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = onLoad, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Load secrets")
                }
            }
        }

        is MainViewModel.State.Success -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Secrets loaded successfully",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "API key: ${state.secrets.serverApiKey}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "API password: ${state.secrets.serverApiPassword}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IntegrityCheckPoCTheme {
        MainContent(state = MainViewModel.State.NotLoaded, onLoad = {})
    }
}