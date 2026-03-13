package com.horgaring.dateapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.horgaring.dateapp.data.api.TokenManager
import com.horgaring.dateapp.data.api.dto.UpdateProfileRequest
import com.horgaring.dateapp.data.repository.DateAppRepository
import com.horgaring.dateapp.ui.components.AvatarImage
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val repository = remember { DateAppRepository() }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf<String?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var isUploading by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                isUploading = true
                try {
                    val inputStream = context.contentResolver.openInputStream(it)
                    val tempFile = File(context.cacheDir, "avatar_upload.jpg")
                    inputStream?.use { input ->
                        tempFile.outputStream().use { output -> input.copyTo(output) }
                    }
                    val result = repository.uploadAvatar(tempFile)
                    avatarUrl = result.avatarUrl
                } catch (_: Exception) { }
                isUploading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        try {
            val profile = repository.getMyProfile()
            firstName = profile.firstName ?: ""
            lastName = profile.lastName ?: ""
            email = profile.email ?: ""
            bio = profile.bio ?: ""
            gender = profile.gender ?: ""
            avatarUrl = profile.avatarUrl
        } catch (_: Exception) {
            firstName = TokenManager.firstName ?: ""
            email = TokenManager.email ?: ""
        }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = {
                    if (isEditing) {
                        scope.launch {
                            try {
                                repository.updateMyProfile(
                                    UpdateProfileRequest(
                                        firstName = firstName.ifBlank { null },
                                        lastName = lastName.ifBlank { null },
                                        bio = bio.ifBlank { null },
                                        gender = gender.ifBlank { null }
                                    )
                                )
                            } catch (_: Exception) { }
                        }
                    }
                    isEditing = !isEditing
                }
            ) {
                Icon(
                    if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                    contentDescription = if (isEditing) "Save" else "Edit"
                )
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            AvatarImage(
                                imageUrl = avatarUrl,
                                name = firstName,
                                size = 120.dp
                            )
                            if (isUploading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(120.dp),
                                    strokeWidth = 3.dp
                                )
                            }
                            FloatingActionButton(
                                onClick = { imagePickerLauncher.launch("image/*") },
                                modifier = Modifier.size(36.dp),
                                shape = CircleShape,
                                containerColor = MaterialTheme.colorScheme.primary
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = "Change photo",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { if (isEditing) firstName = it },
                        label = { Text("First Name") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { if (isEditing) lastName = it },
                        label = { Text("Last Name") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = { if (isEditing) gender = it },
                        label = { Text("Gender") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { if (isEditing) bio = it },
                        label = { Text("Bio") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        maxLines = 4
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = {
                            repository.logout()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log Out")
                    }
                }
            }
        }
    }
}
