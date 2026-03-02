package ru.nikzarch.tldrbot.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import ru.nikzarch.tldrbot.entity.ChatMessage
import ru.nikzarch.tldrbot.llm.client.LlmClient
import ru.nikzarch.tldrbot.llm.dto.SummaryDto
import ru.nikzarch.tldrbot.repository.MessagesRepository
import ru.nikzarch.tldrbot.service.MessagesService
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Service
class MessagesServiceImpl(
    private val repository: MessagesRepository,
    private val llmClient: LlmClient,
    private val objectMapper: ObjectMapper
) : MessagesService {

    companion object {
        private const val MAX_MESSAGES = 500
        private val COOLDOWN = TimeUnit.MINUTES.toMillis(10)
    }

    private val lastSummaryTime = ConcurrentHashMap<Long, Long>()


    override fun saveMessage(
        chatId: Long,
        userId: Long,
        username: String?,
        text: String
    ) {
        val message = ChatMessage(
            chatId = chatId,
            userId = userId,
            username = username,
            text = text
        )
        if (repository.findByChatIdOrderByCreatedAtDesc(chatId, Pageable.unpaged()).count() > MAX_MESSAGES) {
            repository
        }
        repository.save(message)
    }

    override fun summarize(chatId: Long): String {
        val now = Instant.now().toEpochMilli()
        val lastTime = lastSummaryTime[chatId] ?: 0L

        if (now - lastTime < COOLDOWN) {
            val remaining = (COOLDOWN - (now - lastTime)) / 1000
            return "Подождите $remaining секунд перед тем как запросить новое саммари"
        }

        lastSummaryTime[chatId] = now

        val messages = repository
            .findByChatIdOrderByCreatedAtDesc(chatId, Pageable.ofSize(MAX_MESSAGES))

        if (messages.isEmpty()) {
            return "Недостаточно сообщений для саммари."
        }

        val response = llmClient.summarize(messages)
        val summary: SummaryDto = objectMapper.readValue(response.message.content)
        return summary.summary ?: "Во время генерации саммари произошла ошибка"
    }
}