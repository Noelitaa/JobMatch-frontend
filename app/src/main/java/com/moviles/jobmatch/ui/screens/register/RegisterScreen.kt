package com.moviles.jobmatch.ui.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import com.moviles.jobmatch.data.repository.AppContainer
import com.moviles.jobmatch.ui.components.AccountTypeCard
import com.moviles.jobmatch.ui.components.JobMatchTextField
import com.moviles.jobmatch.ui.theme.Background
import com.moviles.jobmatch.ui.theme.BottomNavUnselected
import com.moviles.jobmatch.ui.theme.DarkBlue
import com.moviles.jobmatch.ui.theme.JobMatchTheme
import com.moviles.jobmatch.ui.theme.StatusActive
import com.moviles.jobmatch.ui.theme.StatusInactive

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit = {}
) {
    val registerViewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(AppContainer.authRepository)
    )
    val uiState by registerViewModel.uiState.collectAsStateWithLifecycle()

    var selectedType by remember { mutableStateOf("student") }

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var university by remember { mutableStateOf("") }
    var career by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }

    var companyName by remember { mutableStateOf("") }
    var taxId by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isRegistered) {
        if (uiState.isRegistered) {
            delay(1500L)
            onNavigateToLogin()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(color = DarkBlue, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Work,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "JobMatch",
                style = MaterialTheme.typography.titleMedium,
                color = BottomNavUnselected,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineLarge,
                color = BottomNavUnselected
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Únete a la comunidad de JobMatch hoy mismo.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¿Cómo usarás JobMatch?",
                style = MaterialTheme.typography.bodyMedium,
                color = BottomNavUnselected,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AccountTypeCard(
                    icon = Icons.Default.School,
                    title = "Estudiante",
                    description = "Busco trabajos temporales y pasantías.",
                    isSelected = selectedType == "student",
                    onClick = { selectedType = "student" },
                    modifier = Modifier.weight(1f)
                )
                AccountTypeCard(
                    icon = Icons.Default.Work,
                    title = "Empresa",
                    description = "Busco talento joven para mi negocio.",
                    isSelected = selectedType == "company",
                    onClick = { selectedType = "company" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedType == "student") {
                JobMatchTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = "Nombre Completo",
                    placeholder = "Ej. Juan Pérez",
                    leadingIcon = Icons.Default.Person
                )

                Spacer(modifier = Modifier.height(16.dp))

                JobMatchTextField(
                    value = university,
                    onValueChange = { university = it },
                    label = "Universidad",
                    placeholder = "Ej. Universidad de Costa Rica",
                    leadingIcon = Icons.Default.School
                )

                Spacer(modifier = Modifier.height(16.dp))

                JobMatchTextField(
                    value = career,
                    onValueChange = { career = it },
                    label = "Carrera",
                    placeholder = "Ej. Ingeniería en Sistemas",
                    leadingIcon = Icons.AutoMirrored.Filled.MenuBook
                )

                Spacer(modifier = Modifier.height(16.dp))

                JobMatchTextField(
                    value = studentId,
                    onValueChange = { studentId = it },
                    label = "Cédula",
                    placeholder = "Ej. 118340123",
                    leadingIcon = Icons.Default.Badge
                )

                Spacer(modifier = Modifier.height(16.dp))
            } else {
                JobMatchTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = "Nombre de la Empresa",
                    placeholder = "Ej. Tech Solutions S.A.",
                    leadingIcon = Icons.Default.Business
                )

                Spacer(modifier = Modifier.height(16.dp))

                JobMatchTextField(
                    value = taxId,
                    onValueChange = { taxId = it },
                    label = "Cédula Jurídica",
                    placeholder = "Ej. 3-101-123456",
                    leadingIcon = Icons.Default.Badge
                )

                Spacer(modifier = Modifier.height(16.dp))

                JobMatchTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Teléfono",
                    placeholder = "Ej. 88887777",
                    leadingIcon = Icons.Default.Phone,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BottomNavUnselected
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = {
                            Text(
                                text = "Describe tu empresa...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        },
                        singleLine = false,
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkBlue,
                            unfocusedBorderColor = Color.LightGray,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = DarkBlue
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            JobMatchTextField(
                value = email,
                onValueChange = { email = it },
                label = "Correo Electrónico",
                placeholder = "nombre@ejemplo.com",
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            JobMatchTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                placeholder = "••••••••",
                leadingIcon = Icons.Default.Lock,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                            tint = Color.Gray
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            JobMatchTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirmar Contraseña",
                placeholder = "••••••••",
                leadingIcon = Icons.Default.Lock,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Ocultar" else "Mostrar",
                            tint = Color.Gray
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            val isFormValid = if (selectedType == "student") {
                fullName.isNotBlank() && email.isNotBlank() &&
                    university.isNotBlank() && career.isNotBlank() && studentId.isNotBlank() &&
                    password.isNotBlank() && confirmPassword.isNotBlank()
            } else {
                companyName.isNotBlank() && taxId.isNotBlank() && email.isNotBlank() &&
                    phone.isNotBlank() && description.isNotBlank() &&
                    password.isNotBlank() && confirmPassword.isNotBlank()
            }

            Button(
                onClick = {
                    if (selectedType == "student") {
                        registerViewModel.register(
                            fullName, email, password, confirmPassword,
                            university, career, studentId.ifBlank { null }
                        )
                    } else {
                        registerViewModel.registerCompany(
                            companyName, taxId, email, phone, description, password, confirmPassword
                        )
                    }
                },
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkBlue,
                    contentColor = Color.White,
                    disabledContainerColor = DarkBlue.copy(alpha = 0.5f),
                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Registrarse",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            if (uiState.successMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.successMessage!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = StatusActive,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.errorMessage!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = StatusInactive,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "¿Ya tienes una cuenta? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BottomNavUnselected
                )
                Text(
                    text = "Iniciar sesión",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = buildAnnotatedString {
                    append("Al registrarte, aceptas nuestros ")
                    withStyle(SpanStyle(color = DarkBlue)) {
                        append("Términos de Servicio")
                    }
                    append(" y ")
                    withStyle(SpanStyle(color = DarkBlue)) {
                        append("Política de Privacidad")
                    }
                    append(".")
                },
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = DarkBlue)
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun RegisterScreenPreview() {
    JobMatchTheme {
        RegisterScreen()
    }
}
