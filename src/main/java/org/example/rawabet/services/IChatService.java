package org.example.rawabet.services;

import org.example.rawabet.entities.ChatInstantane;

import java.util.List;

public interface IChatService {

    ChatInstantane sendMessage(ChatInstantane message);

    List<ChatInstantane> getAllMessages();
    ChatInstantane updateMessage(ChatInstantane message);
    void deleteMessage(Long id);
    ChatInstantane getMessageById(Long id);


}