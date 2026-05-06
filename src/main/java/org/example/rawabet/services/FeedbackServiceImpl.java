package org.example.rawabet.services;

import lombok.extern.slf4j.Slf4j;
import org.example.rawabet.cinema.entities.Film;
import org.example.rawabet.cinema.repositories.FilmRepository;
import org.example.rawabet.dto.feedback.ai.FeedbackModerationAiResponse;
import org.example.rawabet.dto.feedback.request.CreateFeedbackRequest;
import org.example.rawabet.dto.feedback.request.UpdateFeedbackRequest;
import org.example.rawabet.dto.feedback.response.FeedbackModerationResponse;
import org.example.rawabet.dto.feedback.response.FeedbackResponse;
import org.example.rawabet.entities.Feedback;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.FeedbackRepository;
import org.example.rawabet.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FeedbackServiceImpl implements IFeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FeedbackModerationAiService feedbackModerationAiService;

    @Override
    public FeedbackResponse addFeedback(CreateFeedbackRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur connecte introuvable"));

        Film film = filmRepository.findById(request.getFilmId())
                .orElseThrow(() -> new RuntimeException("Film not found"));

        Feedback feedback = new Feedback();
        feedback.setNote(request.getNote());
        feedback.setCommentaire(request.getCommentaire());
        feedback.setDate(LocalDate.now());
        feedback.setUser(user);
        feedback.setFilm(film);

        feedback = feedbackRepository.save(feedback);
        return mapToResponse(feedback);
    }

    @Override
    public FeedbackModerationResponse createFeedbackWithModeration(CreateFeedbackRequest request, String connectedUserEmail) {
        log.info("User connecte: {}", connectedUserEmail);
        log.info("Commentaire: {}", request.getCommentaire());

        User connectedUser = userRepository.findByEmail(connectedUserEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        log.info("Email user: {}", connectedUser.getEmail());

        Film film = filmRepository.findById(request.getFilmId())
                .orElseThrow(() -> new RuntimeException("Film not found"));

        boolean containsBadWords = containsBadWords(request.getCommentaire());
        log.info("containsBadWords: {}", containsBadWords);

        if (containsBadWords) {
            return buildModerationBlockedResponse(connectedUser, request.getCommentaire());
        }

        Feedback feedback = new Feedback();
        feedback.setNote(request.getNote());
        feedback.setCommentaire(request.getCommentaire());
        feedback.setDate(LocalDate.now());
        feedback.setUser(connectedUser);
        feedback.setFilm(film);

        Feedback savedFeedback = feedbackRepository.save(feedback);

        return FeedbackModerationResponse.builder()
                .success(true)
                .containsBadWords(false)
                .message("Feedback ajoute avec succes.")
                .feedback(mapToResponse(savedFeedback))
                .build();
    }

    @Override
    public FeedbackModerationResponse updateFeedbackWithModeration(UpdateFeedbackRequest request, String email) {
        Feedback feedback = feedbackRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        if (!feedback.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Non autorise a modifier ce feedback");
        }

        Film film = filmRepository.findById(request.getFilmId())
                .orElseThrow(() -> new RuntimeException("Film not found"));

        boolean containsBadWords = containsBadWords(request.getCommentaire());
        if (containsBadWords) {
            return buildModerationBlockedResponse(feedback.getUser(), request.getCommentaire());
        }

        feedback.setNote(request.getNote());
        feedback.setCommentaire(request.getCommentaire());
        feedback.setFilm(film);

        Feedback updatedFeedback = feedbackRepository.save(feedback);

        return FeedbackModerationResponse.builder()
                .success(true)
                .containsBadWords(false)
                .message("Feedback mis a jour avec succes.")
                .feedback(mapToResponse(updatedFeedback))
                .build();
    }

    @Override
    public FeedbackResponse updateFeedback(UpdateFeedbackRequest request, String email) {
        Feedback feedback = feedbackRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        if (!feedback.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Non autorise a modifier ce feedback");
        }

        Film film = filmRepository.findById(request.getFilmId())
                .orElseThrow(() -> new RuntimeException("Film not found"));

        feedback.setNote(request.getNote());
        feedback.setCommentaire(request.getCommentaire());
        feedback.setFilm(film);

        feedback = feedbackRepository.save(feedback);
        return mapToResponse(feedback);
    }

    @Override
    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }

    @Override
    public FeedbackResponse getById(Long id) {
        Feedback feedback = feedbackRepository.findById(id).orElse(null);
        return feedback != null ? mapToResponse(feedback) : null;
    }

    @Override
    public List<FeedbackResponse> getAll() {
        return feedbackRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackResponse> getMyFeedbacks(String email) {
        return feedbackRepository.findByUserEmail(email)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private FeedbackResponse mapToResponse(Feedback feedback) {
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .note(feedback.getNote())
                .commentaire(feedback.getCommentaire())
                .date(feedback.getDate() != null ? feedback.getDate().toString() : null)
                .userId(feedback.getUser() != null ? feedback.getUser().getId() : null)
                .filmId(feedback.getFilm() != null ? feedback.getFilm().getId() : null)
                .build();
    }

    private boolean containsBadWords(String commentaire) {
        FeedbackModerationAiResponse moderation = feedbackModerationAiService.analyze(commentaire);
        log.info(
                "Moderation model result => hasBadWords={}, score={}, severity={}, model={}",
                moderation.isHasBadWords(),
                moderation.getScore(),
                moderation.getSeverity(),
                moderation.getModel()
        );
        return moderation.isHasBadWords();
    }

    private FeedbackModerationResponse buildModerationBlockedResponse(User user, String commentaire) {
        try {
            log.warn("Envoi mail declenche vers {}", user.getEmail());
            emailService.sendWarningEmail(
                    user.getEmail(),
                    user.getNom(),
                    commentaire
            );

            return FeedbackModerationResponse.builder()
                    .success(false)
                    .containsBadWords(true)
                    .message("Votre feedback contient des mots interdits. Un email d'avertissement a ete envoye.")
                    .feedback(null)
                    .build();
        } catch (Exception exception) {
            log.error("Echec de l'envoi du mail d'avertissement pour {}", user.getEmail(), exception);
            return FeedbackModerationResponse.builder()
                    .success(false)
                    .containsBadWords(true)
                    .message("Votre feedback contient des mots interdits, mais l'envoi du mail a echoue. Verifiez la configuration SMTP.")
                    .feedback(null)
                    .build();
        }
    }
}
