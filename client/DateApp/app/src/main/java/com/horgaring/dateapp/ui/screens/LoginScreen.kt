package com.horgaring.dateapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.horgaring.dateapp.data.ValidationException
import com.horgaring.dateapp.data.repository.DateAppRepository
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val repository = remember { DateAppRepository() }
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var emailErrorMessage by remember { mutableStateOf<String?>(null) }
    var password by remember { mutableStateOf("") }
    var passwordErrorMessage by remember { mutableStateOf<String?>(null) }
    var firstName by remember { mutableStateOf("") }
    var firstNameErrorMessage by remember { mutableStateOf<String?>(null) }
    var lastName by remember { mutableStateOf("") }
    var lastNameErrorMessage by remember { mutableStateOf<String?>(null) }
    var birthDateText by remember { mutableStateOf("") }
    var birthDateErrorMessage by remember { mutableStateOf<String?>(null) }
    var selectedGender by remember { mutableStateOf<String?>(null) }
    var genderErrorMessage by remember { mutableStateOf<String?>(null) }
    var generalErrorMessage by remember { mutableStateOf<String?>(null) }
    var isSignUp by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        birthDateText = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    }
                    showDatePicker = false
                }) { Text("ОК") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "DateApp",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text(
            text = if (isSignUp) "Создать аккаунт" else "Зарегистрироваться",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (isSignUp) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Имя") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )

            if (firstNameErrorMessage != null) {
                Text(
                    text = firstNameErrorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Фамилия") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )

            if (lastNameErrorMessage != null) {
                Text(
                    text = lastNameErrorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            OutlinedTextField(
                value = birthDateText,
                onValueChange = {},
                label = { Text("Дата рождения") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                readOnly = true,
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Text("📅")
                    }
                }
            )

            if (birthDateErrorMessage != null) {
                Text(
                    text = birthDateErrorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedGender == "Муж",
                    onClick = { selectedGender = if (selectedGender == "Муж") null else "Муж" },
                    label = { Text("Муж") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedGender == "Жен",
                    onClick = { selectedGender = if (selectedGender == "Жен") null else "Жен" },
                    label = { Text("Жен") },
                    modifier = Modifier.weight(1f)
                )
            }

            if (genderErrorMessage != null) {
                Text(
                    text = genderErrorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Почта") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        if (emailErrorMessage != null) {
            Text(
                text = emailErrorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        if (passwordErrorMessage != null) {
            Text(
                text = passwordErrorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (generalErrorMessage != null) {
            Text(
                text = generalErrorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = {
                isLoading = true
                emailErrorMessage = null
                passwordErrorMessage = null
                firstNameErrorMessage = null
                lastNameErrorMessage = null
                birthDateErrorMessage = null
                genderErrorMessage = null
                generalErrorMessage = null
                scope.launch {
                    try {
                        if (isSignUp) {
                            if (selectedGender.isNullOrBlank()) {
                                genderErrorMessage = "Выберите пол"
                                isLoading = false
                                return@launch
                            }
                            repository.register(
                                email = email.trim(),
                                password = password,
                                firstName = firstName.trim(),
                                lastName = lastName.trim().ifBlank { null },
                                birthDate = birthDateText,
                                gender = selectedGender
                            )
                        } else {
                            repository.login(email.trim(), password)
                        }
                        navController.navigate("swipe") {
                            popUpTo("login") { inclusive = true }
                        }

                    } catch (e: ValidationException) {
                        val unmapped = mutableListOf<String>()
                        for (entry in e.errors.entries) {
                            when (entry.key) {
                                "email" -> emailErrorMessage = entry.value
                                "password" -> passwordErrorMessage = entry.value
                                "firstName", "first_name" -> firstNameErrorMessage = entry.value
                                "lastName", "last_name" -> lastNameErrorMessage = entry.value
                                "birthDate", "birth_date" -> birthDateErrorMessage = entry.value
                                "gender" -> genderErrorMessage = entry.value
                                else -> unmapped.add(entry.value)
                            }
                        }
                        if (unmapped.isNotEmpty()) {
                            generalErrorMessage = unmapped.joinToString("\n")
                        }
                    } catch (e: Exception) {
                        generalErrorMessage = "Что-то пошло не так. Проверьте подключение к серверу и попробуйте снова."
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(if (isSignUp) "Зарегистрироваться" else "Войти")
            }
        }

        TextButton(
            onClick = {
                isSignUp = !isSignUp
            }
        ) {
            Text(if (isSignUp) "Уже есть аккаунт? Войти" else "Нет аккаунта? Зарегистрироваться")
        }
    }
}
