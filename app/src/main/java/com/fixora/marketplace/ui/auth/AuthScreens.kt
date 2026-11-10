package com.fixora.marketplace.ui.auth

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.i18n.LocalAppStrings
import com.fixora.marketplace.data.local.AppPreferences
import com.fixora.marketplace.data.model.UserRole
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.data.auth.GoogleAuthHelper
import com.fixora.marketplace.data.auth.AuthResult
import kotlinx.coroutines.launch
import com.fixora.marketplace.ui.components.FixoraBrandMark
import com.fixora.marketplace.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Email Realness Validation Helper
// ─────────────────────────────────────────────────────────────────────────────
fun isValidEmail(email: String): Boolean {
    val trimmed = email.trim()
    if (trimmed.length < 5 || trimmed.length > 254) return false
    if (!Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) return false

    val parts = trimmed.split("@")
    if (parts.size != 2) return false
    val userPart = parts[0]
    val domainPart = parts[1]

    if (userPart.isBlank() || domainPart.isBlank()) return false
    if (!domainPart.contains(".")) return false

    val domainSegments = domainPart.split(".")
    if (domainSegments.any { it.isBlank() }) return false

    val tld = domainSegments.last().lowercase()
    if (tld.length < 2 || !tld.all { it.isLetter() }) return false

    val bannedPrefixes = listOf("test", "fake", "none", "asdf", "dummy")
    if (bannedPrefixes.contains(userPart.lowercase()) && bannedPrefixes.contains(domainSegments[0].lowercase())) {
        return false
    }

    return true
}

// ─────────────────────────────────────────────────────────────────────────────
// Splash Screen
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FixoraDarkNavy)
            .clickable {
                onSplashFinished()
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            FixoraPrimary.copy(alpha = 0.20f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            FixoraBrandMark(Modifier.size(96.dp))

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "FIXORA",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 4.sp,
                fontSize = 34.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your Home. Fixed. Fast.",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.75f),
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                color = FixoraPrimary.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "A worldwide marketplace for skilled services",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = FixoraPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    onSplashFinished()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FixoraAction),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = "Get Started",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tap anywhere to continue",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.35f)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            _TrustBadge(icon = Icons.Default.Verified, label = "Skill Verified")
            _TrustBadge(icon = Icons.Default.Security, label = "14-Day Warranty")
            _TrustBadge(icon = Icons.Default.Payments, label = "JazzCash / Card")
        }
    }
}

@Composable
private fun _TrustBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = FixoraPrimary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.height(3.dp))
        Text(label, color = Color.White.copy(alpha = 0.55f), style = MaterialTheme.typography.labelSmall)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Onboarding Screen
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun OnboardingScreen(
    onRoleSelected: (UserRole) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FixoraPageBrush)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FixoraBrandMark(Modifier.size(44.dp))
        Text("Welcome to Fixora", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("What brings you here?", style = MaterialTheme.typography.bodyLarge, color = FixoraTextSecondary)
        Button(onClick = { onRoleSelected(UserRole.CUSTOMER) }, modifier = Modifier.fillMaxWidth()) {
            Text("I need a service")
        }
        OutlinedButton(onClick = { onRoleSelected(UserRole.PROVIDER) }, modifier = Modifier.fillMaxWidth()) {
            Text("I provide a service")
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Login Screen (Clean Name & Valid Email Auth - Direct & Professional)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun LoginScreen(
    onLoginSuccess: (UserRole) -> Unit,
    onVerifyEmail: (String) -> Unit,
    onSkipSignIn: (() -> Unit)? = null,
    initialRole: UserRole = UserRole.CUSTOMER,
    onNavigateBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val strings = LocalAppStrings.current
    var selectedTab by rememberSaveable { mutableIntStateOf(if (onNavigateBack != null) 0 else 1) }

    // Sign Up State
    var selectedRole by rememberSaveable(initialRole) { mutableStateOf(initialRole) }
    var nameInput by rememberSaveable { mutableStateOf("") }
    var emailInput by rememberSaveable { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var selectedCity by rememberSaveable { mutableStateOf("") }
    var areaInput by rememberSaveable { mutableStateOf("") }

    // Freelancer specific state
    var selectedCategories by remember { mutableStateOf(setOf("AC_SERVICE")) }
    var hourlyRateInput by remember { mutableStateOf("1500") }
    var bioInput by remember { mutableStateOf("") }

    // Log In State
    var loginEmailInput by rememberSaveable { mutableStateOf("") }
    var loginEmailError by remember { mutableStateOf<String?>(null) }

    // Google Sign-In state
    var googleLoading by remember { mutableStateOf(false) }
    var googleError by remember { mutableStateOf<String?>(null) }

    val handleGoogleSignIn: () -> Unit = {
        scope.launch {
            googleLoading = true
            googleError = null
            val result = GoogleAuthHelper.signIn(context)
            googleLoading = false
            when (result) {
                is AuthResult.Success -> {
                    val data = result.data
                    val existing = FixoraRepository.findUserByEmail(data.email)
                    val user = if (existing != null) {
                        FixoraRepository.switchUser(existing)
                        existing
                    } else {
                        FixoraRepository.registerUser(
                            name = data.name,
                            email = data.email,
                            role = if (selectedTab == 0) selectedRole else UserRole.CUSTOMER,
                            city = selectedCity.trim(),
                            area = areaInput.trim(),
                            categories = selectedCategories.toList(),
                            bio = bioInput,
                            hourlyRate = hourlyRateInput.toDoubleOrNull() ?: 1500.0,
                            isVerified = true
                        )
                    }
                    AppPreferences.saveUserSession(context, user, isEmailVerified = true)
                    onLoginSuccess(user.role)
                }
                is AuthResult.Error -> {
                    if (result.message != "CANCELLED") {
                        googleError = result.message
                    }
                }
            }
        }
    }

    val serviceCategories = listOf(
        "AC_SERVICE" to "AC & Inverters",
        "ELECTRICAL" to "Electrician",
        "PLUMBING" to "Plumbing",
        "HANDYMAN" to "Handyman & Mounts",
        "PAINTING" to "Painting",
        "CARPENTRY" to "Carpentry",
        "SMART_HOME" to "Smart Home & CCTV"
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FixoraPageBrush)
            .safeDrawingPadding()
            .imePadding()
    ) {
        if (onNavigateBack != null) {
            IconButton(onClick = onNavigateBack, modifier = Modifier.padding(start = 8.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.widthIn(max = 440.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val creatingAccount = selectedTab == 0
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FixoraBrandMark(Modifier.size(38.dp))
                    Text("Fixora", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = FixoraTextPrimary)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    if (creatingAccount) strings.createAccountTab else strings.welcomeBack,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = FixoraTextPrimary
                )

                if (creatingAccount) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedRole == UserRole.CUSTOMER,
                            onClick = { selectedRole = UserRole.CUSTOMER },
                            label = { Text(strings.customerRole) }
                        )
                        FilterChip(
                            selected = selectedRole == UserRole.PROVIDER,
                            onClick = { selectedRole = UserRole.PROVIDER },
                            label = { Text(strings.freelancerRole) }
                        )
                    }
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it; nameError = false },
                        label = { Text(strings.fullNameLabel) },
                        isError = nameError,
                        supportingText = if (nameError) { { Text("Enter your name.") } } else null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                val currentEmailError = if (creatingAccount) emailError else loginEmailError
                OutlinedTextField(
                    value = if (creatingAccount) emailInput else loginEmailInput,
                    onValueChange = {
                        if (creatingAccount) {
                            emailInput = it
                            emailError = null
                        } else {
                            loginEmailInput = it
                            loginEmailError = null
                        }
                    },
                    label = { Text(strings.emailAddressLabel) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = currentEmailError != null,
                    supportingText = currentEmailError?.let { message -> { Text(message) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (creatingAccount) {
                    var showDetails by rememberSaveable { mutableStateOf(false) }
                    TextButton(onClick = { showDetails = !showDetails }) {
                        Text(if (showDetails) "Hide profile details" else "Add profile details (optional)")
                        Icon(if (showDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
                    }
                    if (showDetails) {
                        OutlinedTextField(
                            value = selectedCity, onValueChange = { selectedCity = it },
                            label = { Text(strings.cityLabel) }, singleLine = true, modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = areaInput, onValueChange = { areaInput = it },
                            label = { Text(strings.areaLabel) }, singleLine = true, modifier = Modifier.fillMaxWidth()
                        )
                        if (selectedRole == UserRole.PROVIDER) {
                            Text(strings.yourTradesSkills, style = MaterialTheme.typography.titleSmall, color = FixoraTextPrimary)
                            serviceCategories.forEach { (id, label) ->
                                FilterChip(
                                    selected = id in selectedCategories,
                                    onClick = {
                                        selectedCategories = when {
                                            id !in selectedCategories -> selectedCategories + id
                                            selectedCategories.size > 1 -> selectedCategories - id
                                            else -> selectedCategories
                                        }
                                    },
                                    label = { Text(label) }
                                )
                            }
                            OutlinedTextField(
                                value = hourlyRateInput,
                                onValueChange = { hourlyRateInput = it.filter(Char::isDigit) },
                                label = { Text(strings.hourlyRateInputLabel) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = bioInput, onValueChange = { bioInput = it },
                                label = { Text(strings.bioLabel) }, modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        val email = (if (creatingAccount) emailInput else loginEmailInput).trim()
                        if (creatingAccount && nameInput.isBlank()) {
                            nameError = true
                            return@Button
                        }
                        if (!isValidEmail(email)) {
                            if (creatingAccount) emailError = "Enter a valid email address."
                            else loginEmailError = "Enter a valid email address."
                            return@Button
                        }

                        val user = if (creatingAccount) {
                            FixoraRepository.registerUser(
                                name = nameInput.trim(),
                                email = email,
                                role = selectedRole,
                                city = selectedCity.trim(),
                                area = areaInput.trim(),
                                category = selectedCategories.first(),
                                categories = selectedCategories.toList(),
                                bio = bioInput,
                                hourlyRate = hourlyRateInput.toDoubleOrNull() ?: 1500.0
                            )
                        } else {
                            val existing = FixoraRepository.findUserByEmail(email)
                            if (existing != null) {
                                FixoraRepository.switchUser(existing)
                                existing
                            } else {
                                FixoraRepository.registerUser(
                                    name = email.substringBefore("@").replace('.', ' ').replaceFirstChar { it.uppercase() },
                                    email = email,
                                    role = UserRole.CUSTOMER
                                )
                            }
                        }
                        AppPreferences.saveUserSession(context, user)
                        if (user.isVerified) {
                            onLoginSuccess(user.role)
                        } else {
                            AppPreferences.setPendingVerifyEmail(context, user.email)
                            onVerifyEmail(user.email)
                        }
                    },
                    enabled = !googleLoading,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                ) {
                    Text(if (!creatingAccount) strings.signInBtn else if (selectedRole == UserRole.PROVIDER) strings.registerFreelancerBtn else strings.createAccountBtn)
                }

                OutlinedButton(
                    onClick = handleGoogleSignIn,
                    enabled = !googleLoading,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                ) {
                    if (googleLoading) {
                        CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Text("G", color = Color(0xFF4285F4), fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(if (googleLoading) strings.connectingToGoogle else strings.continueWithGoogle)
                }
                googleError?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = FixoraEmergency) }

                TextButton(
                    onClick = { selectedTab = if (creatingAccount) 1 else 0; googleError = null },
                    enabled = !googleLoading,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(if (creatingAccount) strings.signInTab else strings.createAccountTab)
                }
                if (onSkipSignIn != null) {
                    TextButton(
                        onClick = onSkipSignIn,
                        enabled = !googleLoading,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) { Text("Skip sign-in (debug)") }
                }
            }
        }
    }
}
