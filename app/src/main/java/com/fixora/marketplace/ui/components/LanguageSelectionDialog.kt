package com.fixora.marketplace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.fixora.marketplace.ui.theme.*

data class LanguageOption(
    val code: String,
    val name: String,
    val nativeName: String,
    val flag: String,
    val subtitle: String
)

val SupportedLanguages = listOf(
    LanguageOption(
        code = "en",
        name = "English",
        nativeName = "English",
        flag = "🇬🇧",
        subtitle = "Default system language"
    ),
    LanguageOption(
        code = "ur",
        name = "Urdu",
        nativeName = "اردو",
        flag = "🌐",
        subtitle = "اردو"
    ),
    LanguageOption(
        code = "ar",
        name = "Arabic",
        nativeName = "العربية",
        flag = "🇸🇦",
        subtitle = "اللغة العربية"
    )
)

@Composable
fun LanguageSelectionDialog(
    initialLanguage: String = "English",
    onDismiss: () -> Unit = {},
    onLanguageSelected: (String) -> Unit
) {
    var selectedLang by remember { mutableStateOf(initialLanguage) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = FixoraSurface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(FixoraPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = FixoraPrimary,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = "Select Language",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FixoraTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "زبان منتخب کریں • اختر اللغة",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FixoraTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Language options
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SupportedLanguages.forEach { option ->
                        val isSelected = selectedLang.equals(option.name, ignoreCase = true) ||
                                selectedLang.equals(option.nativeName, ignoreCase = true)

                        val borderColor = if (isSelected) FixoraPrimary else FixoraBorder
                        val bgColor = if (isSelected) FixoraPrimary.copy(alpha = 0.08f) else FixoraSurfaceVariant

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = bgColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedLang = option.name }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = option.flag,
                                    fontSize = 26.sp
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = option.nativeName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = if (isSelected) FixoraPrimary else FixoraTextPrimary
                                        )
                                        if (option.nativeName != option.name) {
                                            Text(
                                                text = " (${option.name})",
                                                fontSize = 13.sp,
                                                color = FixoraTextMuted
                                            )
                                        }
                                    }
                                    Text(
                                        text = option.subtitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = FixoraTextSecondary
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = FixoraPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Confirm Button
                Button(
                    onClick = { onLanguageSelected(selectedLang) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                ) {
                    Text(
                        text = "Continue / جاری رکھیں",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
