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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kurantsov.integritycheck.domain.SecretsSourceType
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
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val state by viewModel.state.collectAsState()
                    var selectedSourceType by remember { mutableStateOf(SecretsSourceType.BACKEND) }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        MainContent(
                            state = state,
                            selectedSourceType = selectedSourceType,
                            onSourceSelectionChanged = { selectedSourceType = it },
                            onLoad = { viewModel.onLoad(selectedSourceType) },
                            onBack = viewModel::onBack,
                        )
                    }
                }
            }
        }
    }
}

private fun SecretsSourceType.toDisplay() = when (this) {
    SecretsSourceType.BACKEND -> "Backend"
    SecretsSourceType.REMOTE_CONFIG -> "Firebase remote config"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    state: MainViewModel.State,
    selectedSourceType: SecretsSourceType,
    onSourceSelectionChanged: (SecretsSourceType) -> Unit,
    onLoad: () -> Unit,
    onBack: () -> Unit,
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
                Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Back")
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
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }) {
                    TextField(
                        value = selectedSourceType.toDisplay(),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        SecretsSourceType.entries.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item.toDisplay()) },
                                onClick = {
                                    onSourceSelectionChanged(item)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Button(onClick = onLoad, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Load secrets")
                }
            }
        }

        is MainViewModel.State.Success -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = buildAnnotatedString {
                        append("Secrets loaded successfully from ")
                        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                        append(selectedSourceType.toDisplay())
                        pop()
                    },
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = buildAnnotatedString {
                        append("API key: ")
                        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                        append(state.secrets.serverApiKey)
                        pop()
                    },
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = buildAnnotatedString {
                        append("API password: ")
                        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                        append(state.secrets.serverApiPassword)
                        pop()
                    },
                    style = MaterialTheme.typography.bodyMedium,
                )
                Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Back")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IntegrityCheckPoCTheme {
        MainContent(
            state = MainViewModel.State.NotLoaded,
            SecretsSourceType.BACKEND,
            onSourceSelectionChanged = {},
            onLoad = {},
            onBack = {},
        )
    }
}