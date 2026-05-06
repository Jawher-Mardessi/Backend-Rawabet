package org.example.rawabet.controllers;

import org.example.rawabet.dto.feedback.request.CreateFeedbackRequest;
import org.example.rawabet.dto.feedback.request.UpdateFeedbackRequest;
import org.example.rawabet.dto.feedback.response.FeedbackModerationResponse;
import org.example.rawabet.dto.feedback.response.FeedbackResponse;
import org.example.rawabet.entities.User;
import org.example.rawabet.services.IFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedbacks")
public class FeedbackController {

    @Autowired
    private IFeedbackService service;

    @PostMapping("/add")
    public FeedbackResponse add(
            @RequestBody CreateFeedbackRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return service.addFeedback(request, user.getEmail());
    }

    @PostMapping("/moderate")
    public ResponseEntity<FeedbackModerationResponse> moderate(
            @RequestBody CreateFeedbackRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String connectedUserEmail = user.getEmail();
        return ResponseEntity.ok(service.createFeedbackWithModeration(request, connectedUserEmail));
    }

    @PutMapping("/update")
    public ResponseEntity<FeedbackModerationResponse> update(
            @RequestBody UpdateFeedbackRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(service.updateFeedbackWithModeration(request, user.getEmail()));
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteFeedback(id);
    }

    @GetMapping("/{id}")
    public FeedbackResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/all")
    public List<FeedbackResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/my")
    public List<FeedbackResponse> getMyFeedbacks(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return service.getMyFeedbacks(user.getEmail());
    }
}
