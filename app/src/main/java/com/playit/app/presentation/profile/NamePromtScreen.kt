package com.playit.app.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun NamePromptScreen(
    onProfileCreated: (Int) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "What is your name?",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                isError = false
            },
            label = { Text("Enter name") },
            isError = isError,
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        if (isError) {
            Text(
                text = "Please enter a name",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (name.isBlank()) {
                    isError = true
                } else {
                    viewModel.createProfile(
                        name = name.trim(),
                        avatarResId = 0,
                        onCreated = { profileId ->
                            onProfileCreated(profileId)
                        }
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Let's Go!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}