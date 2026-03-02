package ru.nikzarch.tldrbot.llm.client

import ru.nikzarch.tldrbot.entity.ChatMessage
import ru.nikzarch.tldrbot.llm.dto.LlmResponse

interface LlmClient {

    fun summarize(messages: List<ChatMessage>): LlmResponse

    fun summarize(messages: List<ChatMessage>, limit: Int): LlmResponse

}
