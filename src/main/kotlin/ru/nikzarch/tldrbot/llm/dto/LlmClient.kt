package ru.nikzarch.tldrbot.llm.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class MessageDto private constructor(
    val role: String,
    val content: String
) {
    constructor(role: LlmRole, content: String) : this(role.string, content)
}

enum class LlmRole(val string: String) {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant")
}

data class LlmRequest(
    val model: String,
    val format: String = "json",
    val messages: List<MessageDto>,
    val stream: Boolean = false,
    val think: Boolean = false,
    val options: LlmRequestOptions = LlmRequestOptions(0.05, 1000)
)

data class LlmRequestOptions(
    val temperature: Double,
    @JsonProperty("num_predict")
    val numPredict: Int
)

data class LlmResponse(
    val message: MessageDto
)

data class SummaryDto(
    val summary: String? = null,
    val topics: List<String>? = null
)