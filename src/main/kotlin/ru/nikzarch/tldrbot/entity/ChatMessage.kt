package ru.nikzarch.tldrbot.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.StandardBasicTypes
import java.sql.Types
import java.time.Instant


@Entity
@Table(name = "chat_messages")
data class ChatMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "chat_id")
    val chatId: Long,
    @Column(name = "user_id")
    val userId: Long,
    val username: String?,
    val text: String,
    @Column(name = "created_at")
    val createdAt: Instant = Instant.now()
)