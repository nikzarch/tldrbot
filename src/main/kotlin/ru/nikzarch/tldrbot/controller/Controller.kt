package ru.nikzarch.tldrbot.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.nikzarch.tldrbot.service.MessagesService

@RestController
class Controller(
    private val messagesService: MessagesService
) {
    @GetMapping("/msg")
    fun putMessage(@RequestParam(value = "message") message: String,
                   @RequestParam(value = "author") author: String){
        messagesService.saveMessage(1, author.hashCode().toLong(),author,message)
    }
    @GetMapping("/summary")
    fun getSummary() : ResponseEntity<String>{
        return ResponseEntity.ok(messagesService.summarize(1))
    }
}