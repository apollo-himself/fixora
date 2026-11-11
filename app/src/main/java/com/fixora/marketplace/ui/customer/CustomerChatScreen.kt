package com.fixora.marketplace.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.model.ChatMessage
import com.fixora.marketplace.data.model.UserRole
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CustomerChatScreen(
    bookingId: String,
    onNavigateBack: () -> Unit
) {
    val currentUser by FixoraRepository.currentUser.collectAsState()
    val bookings by FixoraRepository.bookings.collectAsState()
    val allMessages by FixoraRepository.chatMessages.collectAsState()

    val booking = bookings.firstOrNull { it.id == bookingId }
    val bookingMessages = allMessages.filter { it.bookingId == bookingId }.sortedWith(compareBy<ChatMessage> { it.timestamp }.thenBy { it.id })

    var textInput by remember { mutableStateOf("") }

    val otherPartyName = if (currentUser.role == UserRole.CUSTOMER) {
        booking?.providerName ?: "Technician"
    } else {
        booking?.customerName ?: "Customer"
    }

    val quickReplies = listOf(
        "Where are you right now?",
        "How long until you arrive?",
        "Please ring the bell upon arrival",
        "Will extra parts be needed?",
        "Let me know when completed!"
    )

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = otherPartyName,
                subtitle = "Booking #${bookingId} • Active Chat",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            Surface(
                color = FixoraSurface,
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Quick Reply Chips
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(quickReplies) { chipText ->
                            SuggestionChip(
                                onClick = {
                                    FixoraRepository.sendChatMessage(bookingId, chipText)
                                },
                                label = {
                                    Text(
                                        chipText,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = FixoraTextPrimary
                                    )
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = FixoraSurfaceVariant
                                )
                            )
                        }
                    }

                    HorizontalDivider(color = FixoraDivider)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            placeholder = { Text("Type message...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = FixoraSurfaceVariant,
                                unfocusedContainerColor = FixoraSurfaceVariant,
                                focusedBorderColor = FixoraPrimary,
                                unfocusedBorderColor = FixoraBorder
                            ),
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (textInput.isNotBlank()) {
                                    FixoraRepository.sendChatMessage(bookingId, textInput.trim())
                                    textInput = ""
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = FixoraAction,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(FixoraPageBrush),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Service Brief Banner
            item {
                Surface(
                    color = FixoraSurfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = FixoraPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Fixora Secure Direct Chat. Keep discussions professional and on-platform for 7-day guarantee protection.",
                            style = MaterialTheme.typography.labelSmall,
                            color = FixoraTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            if (bookingMessages.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No messages yet. Send a message to coordinate arrival and entry details.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FixoraTextMuted
                        )
                    }
                }
            } else {
                items(bookingMessages) { message ->
                    val isMe = message.senderId == currentUser.id
                    ChatBubble(message = message, isMe = isMe)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage, isMe: Boolean) {
    val timeString = remember(message.timestamp) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            color = if (isMe) FixoraPrimary else FixoraSurface,
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                if (!isMe) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = message.senderName,
                            style = MaterialTheme.typography.labelSmall,
                            color = FixoraPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        if (message.isAutoReply) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = FixoraPrimaryLight,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Auto-Reply",
                                    fontSize = 9.sp,
                                    color = FixoraPrimary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isMe) Color.White else FixoraTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timeString,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = if (isMe) Color.White.copy(alpha = 0.7f) else FixoraTextMuted,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
