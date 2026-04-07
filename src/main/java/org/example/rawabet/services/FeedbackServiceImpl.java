package org.example.rawabet.services;

import org.example.rawabet.dto.feedback.request.CreateFeedbackRequest;
import org.example.rawabet.dto.feedback.request.UpdateFeedbackRequest;
import org.example.rawabet.dto.feedback.response.FeedbackResponse;
import org.example.rawabet.entities.Feedback;
import org.example.rawabet.entities.Film;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.FeedbackRepository;
import org.example.rawabet.repositories.FilmRepository;
import org.example.rawabet.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackServiceImpl implements IFeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Override
    public FeedbackResponse addFeedback(CreateFeedbackRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

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
    public FeedbackResponse updateFeedback(UpdateFeedbackRequest request) {
        Feedback feedback = feedbackRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Film film = filmRepository.findById(request.getFilmId())
                .orElseThrow(() -> new RuntimeException("Film not found"));

        feedback.setNote(request.getNote());
        feedback.setCommentaire(request.getCommentaire());
        feedback.setUser(user);
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
}