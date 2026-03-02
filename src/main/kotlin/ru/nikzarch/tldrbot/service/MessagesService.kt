package ru.nikzarch.tldrbot.service

interface MessagesService {
    fun saveMessage(
        chatId: Long,
        userId: Long,
        username: String?,
        text: String
    )

    fun summarize(chatId: Long): String
}