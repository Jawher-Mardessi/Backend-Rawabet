package org.example.rawabet.controllers;

import org.example.rawabet.entities.ChatInstantane;
import org.example.rawabet.services.IChatInstantaneService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")

public class ChatInstantaneController {

    private final IChatInstantaneService chatService;

    public ChatInstantaneController(IChatInstantaneService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/add")
    public ChatInstantane addMessage(@RequestBody ChatInstantane message) {
        return chatService.addMessage(message);
    }

    @PutMapping("/update")
    public ChatInstantane updateMessage(@RequestBody ChatInstantane message) {
        return chatService.updateMessage(message);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteMessage(@PathVariable Long id) {
        chatService.deleteMessage(id);
    }

    @GetMapping("/get/{id}")
    public ChatInstantane getMessage(@PathVariable Long id) {
        return chatService.getMessageById(id);
    }

    @GetMapping("/all")
    public List<ChatInstantane> getAllMessages() {
        return chatService.getAllMessages();
    }

    @GetMapping("/seance/{id}")
    public List<ChatInstantane> getMessagesBySeance(@PathVariable Long id) {
        return chatService.getMessagesBySeance(id);
    }

    @GetMapping("/user/{id}")
    public List<ChatInstantane> getMessagesByUser(@PathVariable Long id) {
        return chatService.getMessagesByUser(id);
    }

}