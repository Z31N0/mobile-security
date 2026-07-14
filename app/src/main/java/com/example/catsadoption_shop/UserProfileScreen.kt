package com.example.catsadoption_shop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.catsadoption_shop.data.AdoptedCat
import com.example.catsadoption_shop.data.InsecureApiClient
import com.example.catsadoption_shop.data.UserProfile
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userId: Int,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var profile by remember { mutableStateOf<UserProfile?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    if (userId <= 0) {
        Text(
            text = "Invalid user ID: $userId",
            color = Color.Red,
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    LaunchedEffect(userId) {
        scope.launch {
            try {
                val response = InsecureApiClient.api.getUser(userId)
                if (response.isSuccessful) {
                    profile = response.body()
                    errorMessage = null
                } else {
                    val err = response.errorBody()?.string() ?: "Unknown error"
                    errorMessage = "Server error: ${response.code()}\n$err"
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (profile?.role == "owner") "Customer Service" else "User Profile",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = Color(0xFFF6F0FF)
        ) {
            when {
                isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }

                errorMessage != null -> Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )

                profile != null -> ProfileContent(profile!!)
            }
        }
    }
}

@Composable
private fun ProfileContent(profile: UserProfile) {
    val isOwner = profile.role == "owner"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF7C4DFF), Color(0xFFE1BEE7))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Avatar(null)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (!isOwner) {
                    Text("User ID: ${profile.id}", color = Color.White)
                    Text("Birthdate: ${profile.birthdate}", color = Color.White)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        ContactInfoCard(profile)
        Spacer(Modifier.height(16.dp))
        if (!isOwner) AdoptedCatsSection(profile.adoptedCats)
    }
}

@Composable
private fun ContactInfoCard(profile: UserProfile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Contact Information", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("📧 Email: ${profile.email}")
            Text("📞 Phone: ${profile.phone}")
            if (profile.role == "owner") {
                Spacer(Modifier.height(6.dp))
                Text("🏬 Role: User Agent", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun AdoptedCatsSection(adoptedCats: List<AdoptedCat>) {
    Text(
        text = "Adopted Cats",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(16.dp)
    )

    if (adoptedCats.isEmpty()) {
        Text(
            text = "This user has not adopted any cats yet.",
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    Column {
        adoptedCats.forEach { cat -> CatCard(cat) }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun Avatar(url: String?) {
    val modifier = Modifier.size(96.dp).clip(CircleShape)

    if (url != null) {
        Image(
            painter = rememberAsyncImagePainter(url),
            contentDescription = "Avatar",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier.background(Color(0xFFB39DDB)),
            contentAlignment = Alignment.Center
        ) {
            Text("😺", fontSize = MaterialTheme.typography.headlineMedium.fontSize)
        }
    }
}

@Composable
private fun CatCard(cat: AdoptedCat) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(cat.imageUrl),
                contentDescription = "Cat ${cat.id}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = "🐱 Cat ID: ${cat.id}",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
