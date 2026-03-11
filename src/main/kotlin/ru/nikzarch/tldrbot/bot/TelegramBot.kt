package ru.nikzarch.tldrbot.bot

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.objects.Update
import ru.nikzarch.tldrbot.bot.command.CommandDispatcher
import kotlin.math.absoluteValue

private val logger = KotlinLogging.logger {}

@Component
@EnableConfigurationProperties(TelegramBotProperties::class)
class TelegramBot(
    private val properties: TelegramBotProperties,
    private val commandDispatcher: CommandDispatcher
) : LongPollingSingleThreadUpdateConsumer, SpringLongPollingBot {

    override fun consume(update: Update?) {
        update ?: return

        logger.info { "Got update: $update" }

        val message = update.message ?: return

        if (message.chat.type != "group" && message.chat.type != "supergroup") {
            return
        }

        if (message.chat.id.absoluteValue.toString() !in properties.supportedChatIds) {
            return
        }

        commandDispatcher.process(update)
    }

    override fun getBotToken(): String = properties.token

    override fun getUpdatesConsumer(): LongPollingUpdateConsumer = this
}

@ConfigurationProperties(prefix = "telegram.bot")
data class TelegramBotProperties(
    val token: String = "",
    val name: String = "",
    val supportedChatIds: List<String> = emptyList()
)
