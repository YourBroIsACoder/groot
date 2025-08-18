package com.example.groot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isFromUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen() {
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    if (messages.isEmpty()) {
        messages.add(ChatMessage("Hello! I'm Gardenia's virtual assistant. Ask me about hours, delivery, or beginner plants!", false))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gardening Assistant") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            MessageInput(
                onSendClicked = { text ->
                    messages.add(ChatMessage(text, true))
                    val botResponse = getBotResponse(text)
                    messages.add(ChatMessage(botResponse, false))
                    coroutineScope.launch {
                        listState.animateScrollToItem(messages.size - 1)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(message = msg)
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val bubbleColor = if (message.isFromUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val alignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = message.text,
            modifier = Modifier
                .align(alignment)
                .clip(RoundedCornerShape(12.dp))
                .background(bubbleColor)
                .padding(12.dp)
        )
    }
}

@Composable
fun MessageInput(onSendClicked: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Ask a question...") },
            shape = RoundedCornerShape(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = {
                if (text.isNotBlank()) {
                    onSendClicked(text)
                    text = ""
                }
            },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Default.Send,
                contentDescription = "Send message",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

fun getBotResponse(userInput: String): String {
    val lowerCaseInput = userInput.lowercase()
    return when {
        "hour" in lowerCaseInput -> "We are open from 9 AM to 6 PM, Monday to Saturday."
        "deliver" in lowerCaseInput -> "Yes, we offer local delivery within a 10-mile radius for a small fee."
        "beginner" in lowerCaseInput -> "Snake Plants, Pothos, and ZZ Plants are great for beginners! They are very low-maintenance."
        "hello" in lowerCaseInput || "hi" in lowerCaseInput -> "Hello! How can I help you with your gardening needs today?"
        "thank" in lowerCaseInput -> "You're welcome! Let me know if you have more questions."
        else -> "I'm sorry, I don't have an answer for that. You can try asking about our hours, delivery, or beginner plants."
    }
}