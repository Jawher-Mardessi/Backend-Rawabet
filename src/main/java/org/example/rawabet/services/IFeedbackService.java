package org.example.rawabet.services;

import org.example.rawabet.dto.feedback.request.CreateFeedbackRequest;
import org.example.rawabet.dto.feedback.request.UpdateFeedbackRequest;
import org.example.rawabet.dto.feedback.response.FeedbackResponse;

import java.util.List;

public interface IFeedbackService {

    FeedbackResponse addFeedback(CreateFeedbackRequest request);

    FeedbackResponse updateFeedback(UpdateFeedbackRequest request);

    void deleteFeedback(Long id);

    FeedbackResponse getById(Long id);

    List<FeedbackResponse> getAll();
}