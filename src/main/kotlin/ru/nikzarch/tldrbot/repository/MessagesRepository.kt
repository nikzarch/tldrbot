package ru.nikzarch.tldrbot.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.nikzarch.tldrbot.entity.ChatMessage


@Repository
interface MessagesRepository : JpaRepository<ChatMessage, Long> {
    fun findByChatIdOrderByCreatedAtDesc(chatId: Long?, pageable: Pageable): List<ChatMessage>

    @Query(
        value = """
       SELECT COUNT(*)
       FROM message
       WHERE chat_id = :chatId
       """, nativeQuery = true
    )
    fun countByChatId(@Param("chatId") chatId: Long): Long

    @Transactional
    @Modifying
    @Query(
        value = """
            DELETE FROM chat_messages 
            WHERE chat_id = :chatId 
            AND id NOT IN (
                SELECT id 
                FROM chat_messages 
                WHERE chat_id = :chatId 
                ORDER BY created_at DESC 
                LIMIT :n
            )
        """,
        nativeQuery = true
    )
    fun deleteAllExceptLastN(chatId: Long, n: Int)
}