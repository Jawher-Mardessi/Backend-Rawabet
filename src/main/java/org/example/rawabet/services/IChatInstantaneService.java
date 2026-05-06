package org.example.rawabet.services;

import org.example.rawabet.entities.ChatInstantane;

import java.util.List;

public interface IChatInstantaneService {

    ChatInstantane addMessage(ChatInstantane message);

    ChatInstantane updateMessage(ChatInstantane message);

    void deleteMessage(Long id);

    ChatInstantane getMessageById(Long id);

    List<ChatInstantane> getAllMessages();

    List<ChatInstantane> getMessagesBySeance(Long seanceId);

    List<ChatInstantane> getMessagesByUser(Long userId);

}