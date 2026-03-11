package ru.nikzarch.tldrbot.bot.command

import org.telegram.telegrambots.meta.api.objects.Update


interface Command {
    fun supports(update: Update): Boolean
    suspend fun execute(update: Update)
}