package org.example.rawabet.controllers;

import org.example.rawabet.dto.feedback.request.CreateFeedbackRequest;
import org.example.rawabet.dto.feedback.request.UpdateFeedbackRequest;
import org.example.rawabet.dto.feedback.response.FeedbackResponse;
import org.example.rawabet.services.IFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedbacks")
public class FeedbackController {

    @Autowired
    private IFeedbackService service;

    @PostMapping("/add")
    public FeedbackResponse add(@RequestBody CreateFeedbackRequest request){
        return service.addFeedback(request);
    }

    @PutMapping("/update")
    public FeedbackResponse update(@RequestBody UpdateFeedbackRequest request){
        return service.updateFeedback(request);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id){
        service.deleteFeedback(id);
    }

    @GetMapping("/{id}")
    public FeedbackResponse getById(@PathVariable Long id){
        return service.getById(id);
    }

    @GetMapping("/all")
    public List<FeedbackResponse> getAll(){
        return service.getAll();
    }
}