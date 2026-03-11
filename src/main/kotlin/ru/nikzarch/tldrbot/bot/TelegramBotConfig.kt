package ru.nikzarch.tldrbot.bot


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.generics.TelegramClient

@Configuration
class TelegramClientConfig {

    @Bean
    fun telegramClient(properties: TelegramBotProperties): TelegramClient {
        return OkHttpTelegramClient(properties.token)
    }
}
