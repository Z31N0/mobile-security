package com.example.catsadoption_shop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import javax.net.ssl.SSLHandshakeException
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color


@Composable
fun UserDashboardScreen(
    username: String,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()

    // Regular cats
    var adoptedCats by remember { mutableStateOf<List<CatResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Secure API state
    var secureData by remember { mutableStateOf<SecureCatData?>(null) }
    var secureError by remember { mutableStateOf<String?>(null) }
    var isLoadingSecure by remember { mutableStateOf(true) }

    // Load regular cats
    LaunchedEffect(Unit) {
        scope.launch {
            adoptedCats = CatRepository.fetchCats().take(5)
            isLoading = false
        }
    }

    // Load SECURE API data
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                secureData = SecureApiClient.api.getSecureCat()
            } catch (e: SSLHandshakeException) {
                secureError = "SSL pinning blocked a MITM attack."
            } catch (e: Exception) {
                secureError = "Secure request failed: ${e.message}"
            } finally {
                isLoadingSecure = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        //----------------------------------------------------
        // HEADER + LOGOUT
        //----------------------------------------------------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Welcome $username 🐾", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Your adopted cats:", style = MaterialTheme.typography.titleMedium)
            }

            Button(
                onClick = onLogout,
                modifier = Modifier.alignByBaseline()
            ) {
                Text("Logout")
            }
        }

        Spacer(Modifier.height(12.dp))

        //----------------------------------------------------
        // REGULAR ADOPTED CATS
        //----------------------------------------------------
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(adoptedCats) { cat ->
                    AdoptedCatItem(cat)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        //----------------------------------------------------
        // SECURE API SECTION (Commit 3 requirement)
        //----------------------------------------------------
        Text(
            "Secure API Data",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(10.dp))

        when {
            isLoadingSecure -> {
                CircularProgressIndicator()
            }

            secureData != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Column(modifier = Modifier.padding(11.dp)) {
                        Text("ID: ${secureData!!.id}")
                        Text("Name: ${secureData!!.name}")
                        Text("Description: ${secureData!!.description}")
                    }
                }
            }

            secureError != null -> {
                Text(
                    text = secureError!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun AdoptedCatItem(cat: CatResponse) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            AsyncImage(
                model = cat.url,
                contentDescription = "Adopted cat",
                modifier = Modifier.size(90.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Cat ID: ${cat.id}", style = MaterialTheme.typography.titleMedium)
                Text("Resolution: ${cat.width ?: "?"}×${cat.height ?: "?"}")
            }
        }
    }
}
