package org.example.rawabet.services;

import org.example.rawabet.dto.feedback.request.CreateFeedbackRequest;
import org.example.rawabet.dto.feedback.request.UpdateFeedbackRequest;
import org.example.rawabet.dto.feedback.response.FeedbackModerationResponse;
import org.example.rawabet.dto.feedback.response.FeedbackResponse;

import java.util.List;

public interface IFeedbackService {
    FeedbackResponse addFeedback(CreateFeedbackRequest request, String email);
    FeedbackModerationResponse createFeedbackWithModeration(CreateFeedbackRequest request, String connectedUserEmail);
    FeedbackModerationResponse updateFeedbackWithModeration(UpdateFeedbackRequest request, String email);
    FeedbackResponse updateFeedback(UpdateFeedbackRequest request, String email);
    void deleteFeedback(Long id);
    FeedbackResponse getById(Long id);
    List<FeedbackResponse> getAll();
    List<FeedbackResponse> getMyFeedbacks(String email);
}
