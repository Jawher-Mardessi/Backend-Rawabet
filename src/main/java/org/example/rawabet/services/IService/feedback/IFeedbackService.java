package org.example.rawabet.services.IService.feedback;

import org.example.rawabet.entities.Feedback;

import java.util.List;

public interface IFeedbackService {

    Feedback addFeedback(Feedback feedback);

    Feedback updateFeedback(Feedback feedback);

    void deleteFeedback(Long id);

    Feedback getById(Long id);

    List<Feedback> getAll();
}