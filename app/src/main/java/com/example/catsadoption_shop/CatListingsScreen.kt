package com.example.catsadoption_shop

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch


@Composable
fun CatListingsScreen(
    onLoginClick: () -> Unit,
    onCatCommunicatorClick: () -> Unit,
    onAdminClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var cats by remember { mutableStateOf<List<CatResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val isRooted = remember { isDeviceRooted() }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                cats = CatRepository.fetchCats()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Error fetching cats"
            } finally {
                isLoading = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Welcome to CatsAdoption Shop 🐱",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Text("Error: $errorMessage")
            }
            else -> {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(cats) { cat ->
                        CatItem(cat)
                    }
                }
            }
        }

        // --- BUTTONS --- //
        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            Text("Login to Your Dashboard")
        }

        Button(
            onClick = {
                if (isRooted) {
                    Toast.makeText(context, "Security violation detected. Feature disabled.", Toast.LENGTH_SHORT).show()
                } else {
                    onCatCommunicatorClick()
                }
            },
            enabled = !isRooted,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            Text("Cat Communicator")
        }

        Button(
            onClick = onProfileClick,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            Text("Customer Service")
        }

        if (AppConfig.IS_ADMIN_ENABLED) {
            Button(
                onClick = onAdminClick,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text("Admin Dashboard")
            }
        }
    }
}



@Composable
fun CatItem(cat: CatResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { /* navigate to detail later */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = cat.url,
            contentDescription = "Cat image",
            modifier = Modifier.size(100.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text("Cat ID: ${cat.id}", style = MaterialTheme.typography.titleMedium)
            Text("Resolution: ${cat.width ?: "?"}×${cat.height ?: "?"}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CatListingsScreenPreview() {
    CatListingsScreen(
        onLoginClick = {},
        onCatCommunicatorClick = {},
        onAdminClick = {},
        onProfileClick = {} // <<< I HAVE ADDED THIS LINE
    )
}
