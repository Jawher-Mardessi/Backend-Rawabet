package org.example.rawabet.services;
import org.example.rawabet.entities.ChatInstantane;
import org.example.rawabet.entities.Seance;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.ChatInstantaneRepository;
import org.example.rawabet.repositories.SeanceRepository;
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.services.IChatInstantaneService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatInstantaneServiceImpl implements IChatInstantaneService {

    private final ChatInstantaneRepository chatRepository;
    private final UserRepository userRepository;
    private final SeanceRepository seanceRepository;

    public ChatInstantaneServiceImpl(
            ChatInstantaneRepository chatRepository,
            UserRepository userRepository,
            SeanceRepository seanceRepository) {

        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.seanceRepository = seanceRepository;
    }

    @Override
    public ChatInstantane addMessage(ChatInstantane message) {

        User user = userRepository
                .findById(message.getUser().getId())
                .orElseThrow();

        Seance seance = seanceRepository
                .findById(message.getSeance().getId())
                .orElseThrow();

        message.setUser(user);
        message.setSeance(seance);

        message.setHorodatage(LocalDateTime.now());
        message.setActive(true);

        return chatRepository.save(message);
    }

    @Override
    public ChatInstantane updateMessage(ChatInstantane message) {
        return chatRepository.save(message);
    }

    @Override
    public void deleteMessage(Long id) {
        chatRepository.deleteById(id);
    }

    @Override
    public ChatInstantane getMessageById(Long id) {
        return chatRepository.findById(id).orElse(null);
    }

    @Override
    public List<ChatInstantane> getAllMessages() {
        return chatRepository.findAll();
    }

    @Override
    public List<ChatInstantane> getMessagesBySeance(Long seanceId) {
        return chatRepository.findBySeanceId(seanceId);
    }

    @Override
    public List<ChatInstantane> getMessagesByUser(Long userId) {
        return chatRepository.findByUserId(userId);
    }
}