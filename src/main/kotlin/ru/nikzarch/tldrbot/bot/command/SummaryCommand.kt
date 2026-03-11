package ru.nikzarch.tldrbot.bot.command

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient
import ru.nikzarch.tldrbot.bot.TelegramBotProperties
import ru.nikzarch.tldrbot.service.MessagesService


@Component
class SummaryCommand(
    private val messagesService: MessagesService,
    private val telegramClient: TelegramClient,
    private val properties: TelegramBotProperties
) : Command {

    override fun supports(update: Update): Boolean {
        val message = update.message ?: return false
        val text = message.text ?: return false

        val isCommand = message.entities?.any { it.type == "bot_command" } ?: false
        if (!isCommand) return false

        val rawCommand = text.substringBefore(" ")
        val commandWithoutMention = rawCommand.substringBefore("@${properties.name}")

        return commandWithoutMention == "/summary"
    }

    override suspend fun execute(update: Update) {
        val message = update.message ?: return
        val chatId = message.chatId

        val summary = withContext(Dispatchers.IO) {
            messagesService.summarize(chatId)
        }

        val response = SendMessage.builder()
            .chatId(chatId.toString())
            .text(summary)
            .build()

        withContext(Dispatchers.IO) {
            withTimeout(300_000) {
                telegramClient.execute(response)
            }
        }
    }
}