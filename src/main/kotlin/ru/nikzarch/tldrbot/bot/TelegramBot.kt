package ru.nikzarch.tldrbot.bot

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient
import ru.nikzarch.tldrbot.service.MessagesService
import kotlin.math.absoluteValue

private val logger = KotlinLogging.logger {}

@Component
@EnableConfigurationProperties(TelegramBotProperties::class)
class TelegramBot(
    private val properties: TelegramBotProperties,
    private val messagesService: MessagesService,
    private val telegramClient: TelegramClient = OkHttpTelegramClient(properties.token)
) : LongPollingSingleThreadUpdateConsumer, SpringLongPollingBot {

    private fun saveMessage(msg: Message) {
        messagesService.saveMessage(
            chatId = msg.chatId,
            userId = msg.from.id,
            username = msg.from.firstName,
            text = msg.text
        )
    }

    private fun handleSummary(chatId: Long) {

        val summary = messagesService.summarize(chatId)

        val response = SendMessage.builder()
            .chatId(chatId.toString())
            .text(summary)
            .build()

        telegramClient.execute(response)

    }

    override fun consume(update: Update?) {
        logger.info { "got an update $update" }
        val msg = update?.message ?: return
        val text = msg.text ?: return

        logger.info { "got a text $text" }
        logger.info { " got a chat ${msg.chat}" }

        if (msg.chat.type != "group" && msg.chat.type != "supergroup") {
            return
        }

        if (msg.chat.id.absoluteValue.toString() !in properties.supportedChatIds) return

        val isCommand = msg.entities?.any { it.type == "bot_command" } ?: false

        if (isCommand) {
            val command = text.substringBefore("@" + properties.name)
            when (command) {
                "/summary" -> handleSummary(msg.chatId)
                else -> logger.warn { "Unknown command: $command" }
            }
        } else {
            saveMessage(msg)
        }
    }

    override fun getBotToken(): String = properties.token

    override fun getUpdatesConsumer(): LongPollingUpdateConsumer = this
}


@ConfigurationProperties(prefix = "telegram.bot")
data class TelegramBotProperties(
    val token: String = "",
    val name: String = "",
    val supportedChatIds: List<String> = listOf()
)