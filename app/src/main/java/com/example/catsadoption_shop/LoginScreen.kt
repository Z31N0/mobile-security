package com.example.catsadoption_shop

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.catsadoption_shop.data.AppDatabase
import com.example.catsadoption_shop.data.User
import com.example.catsadoption_shop.data.UserRepository
import com.example.catsadoption_shop.security.WeakEncryption
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // This is where you use the database components.
    val userRepository = remember {
        val userDao = AppDatabase.getDatabase(context).userDao()
        UserRepository(userDao)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("User Account", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        Spacer(Modifier.height(16.dp))

        if (message.isNotEmpty()) {
            val messageColor = if (message.contains("successful")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            Text(message, color = messageColor)
            Spacer(Modifier.height(8.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Login Button Logic
            Button(onClick = {
                coroutineScope.launch {
                    if (username.isBlank() || password.isBlank()) {
                        message = "Username and password cannot be empty."
                        return@launch
                    }
                    // This is where the Frida bypassable check is triggered via the repository.
                    val user = userRepository.getUserByUsername(username)

                    if (user != null) {
                        val decryptedPass = WeakEncryption.decrypt(user.encryptedPassword)
                        if (decryptedPass == password) {
                            message = "Login successful!"
                            onLoginSuccess(username) // Navigate to the next screen
                        } else {
                            message = "Invalid username or password."
                        }
                    } else {
                        // The user is null either because they don't exist OR the root check failed.
                        // This provides a vague error message to the user, as is common.
                        message = "Invalid login, or the device might be rooted."
                    }
                }
            }) {
                Text("Login")
            }

            // Register Button Logic
            Button(onClick = {
                coroutineScope.launch {
                    if (username.isNotBlank() && password.isNotBlank()) {
                        val existingUser = userRepository.getUserByUsername(username)
                        if (existingUser != null) {
                            message = "Username already taken."
                        } else {
                            val encryptedPassword = WeakEncryption.encrypt(password)
                            val newUser = User(username = username, encryptedPassword = encryptedPassword)
                            userRepository.registerUser(newUser)
                            message = "Registration successful! Please log in."
                        }
                    } else {
                        message = "Username and password are required for registration."
                    }
                }
            }) {
                Text("Register")
            }
        }
    }
}
