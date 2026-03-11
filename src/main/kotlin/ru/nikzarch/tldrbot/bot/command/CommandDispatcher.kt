package ru.nikzarch.tldrbot.bot.command

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.nikzarch.tldrbot.service.MessagesService

private val logger = KotlinLogging.logger {}

@Component
class CommandDispatcher(
    private val messagesService: MessagesService,
    private val commands: List<Command>,
) {
    private val botScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun process(update: Update) {
        botScope.launch {
            handle(update)
        }
    }

    private suspend fun handle(update: Update) {

        val command = commands.firstOrNull { it.supports(update) }

        if (command != null) {
            logger.info { "Command matched: ${command::class.simpleName}" }
            command.execute(update)
            return
        }

        update.message?.let { saveMessage(it) }

    }

    private fun saveMessage(message: Message) {
        val text = message.text ?: return

        messagesService.saveMessage(
            chatId = message.chatId,
            userId = message.from.id,
            username = message.from.firstName,
            text = text
        )

    }
}