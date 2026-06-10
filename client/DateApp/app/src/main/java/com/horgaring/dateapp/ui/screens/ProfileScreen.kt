package com.horgaring.dateapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.horgaring.dateapp.data.api.TokenManager
import com.horgaring.dateapp.data.api.dto.UpdateProfileRequest
import com.horgaring.dateapp.data.repository.DateAppRepository
import com.horgaring.dateapp.ui.components.AvatarImage
import com.horgaring.dateapp.ui.util.DateFormatter
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
    var birthDate by remember { mutableStateOf<String?>(null) }
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
                    avatarUrl = result.avatarUrl?.replace("://localhost:", "://10.0.2.2:")
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
            birthDate = profile.birthDate
            avatarUrl = profile.avatarUrl?.replace("://localhost:", "://10.0.2.2:")
        } catch (_: Exception) {
            firstName = TokenManager.firstName ?: "Дима"
            lastName = "Растоврцев"
            email = TokenManager.email ?: "rastvorcev6123hh@mail.ru"
            bio = "Люблю путешествовать, программировать и играть в настольные игры. Мечтаю объехать весь мир!"
            gender = "MALE"
            birthDate = "2000-06-15"
            avatarUrl = null
        }
        isLoading = false
    }

    val fullName = listOfNotNull(firstName, lastName).joinToString(" ").ifBlank { "Ваше имя" }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // ── Header ───────────────────────────────────────────
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(top = 8.dp, bottom = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                IconButton(
                                    onClick = { navController.navigateUp() },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(contentAlignment = Alignment.BottomEnd) {
                                AvatarImage(
                                    imageUrl = avatarUrl,
                                    name = firstName,
                                    size = 120.dp,
                                    modifier = Modifier
                                        .border(
                                            4.dp,
                                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                                            CircleShape
                                        )
                                )
                                if (isUploading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(120.dp),
                                        strokeWidth = 3.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                                FloatingActionButton(
                                    onClick = { imagePickerLauncher.launch("image/*") },
                                    modifier = Modifier.size(36.dp),
                                    shape = CircleShape,
                                    containerColor = MaterialTheme.colorScheme.onPrimary,
                                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.CameraAlt,
                                        contentDescription = "Сменить фото",
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = fullName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (email.isNotBlank()) {
                                Text(
                                    text = email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                // ── Info Cards ──────────────────────────────────────
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .offset(y = (-24).dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (isEditing) {
                            // ── Edit Mode ────────────────────────────
                            OutlinedTextField(
                                value = firstName,
                                onValueChange = { firstName = it },
                                label = { Text("Имя") },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = lastName,
                                onValueChange = { lastName = it },
                                label = { Text("Фамилия") },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = gender,
                                onValueChange = { gender = it },
                                label = { Text("Пол") },
                                leadingIcon = {
                                    Icon(Icons.Default.Face, contentDescription = null)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = birthDate ?: "",
                                onValueChange = { },
                                label = { Text("Дата рождения") },
                                leadingIcon = {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = false,
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = email,
                                onValueChange = { },
                                label = { Text("Почта") },
                                leadingIcon = {
                                    Icon(Icons.Default.Email, contentDescription = null)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = false,
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = bio,
                                onValueChange = { if (bio.length < 500) bio = it },
                                label = { Text("О себе") },
                                leadingIcon = {
                                    Icon(Icons.Default.Info, contentDescription = null)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 4,
                                shape = RoundedCornerShape(12.dp)
                            )
                        } else {
                            // ── View Mode ────────────────────────────
                            ProfileInfoCard(
                                icon = Icons.Default.Face,
                                label = "Пол",
                                value = mapGender(gender).ifBlank { "—" }
                            )

                            ProfileInfoCard(
                                icon = Icons.Default.CalendarMonth,
                                label = "Дата рождения",
                                value = birthDate?.let { DateFormatter.formatBirthDate(it) } ?: "—"
                            )

                            if (bio.isNotBlank()) {
                                ProfileInfoCard(
                                    icon = Icons.Default.Info,
                                    label = "О себе",
                                    value = bio
                                )
                            }
                        }
                    }
                }

                // ── Logout ─────────────────────────────────────────
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedButton(
                            onClick = {
                                repository.logout()
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                            )
                        ) {
                            Icon(
                                Icons.Default.Logout,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Выйти", fontWeight = FontWeight.Medium)
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }

        // ── Floating Edit Button ──────────────────────────────────
        FloatingActionButton(
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
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
        ) {
            Icon(
                if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                contentDescription = if (isEditing) "Сохранить" else "Редактировать",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

private fun mapGender(gender: String): String = when (gender.uppercase()) {
    "MALE" -> "Мужской"
    "FEMALE" -> "Женский"
    else -> gender
}

@Composable
private fun ProfileInfoCard(
    icon: ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}
