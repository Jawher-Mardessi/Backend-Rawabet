package org.example.rawabet.controllers;

import org.example.rawabet.entities.Feedback;
import org.example.rawabet.services.IService.feedback.IFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedbacks")
public class FeedbackController {

    @Autowired
    private IFeedbackService service;

    @PostMapping("/add")
    public Feedback add(@RequestBody Feedback f){
        return service.addFeedback(f);
    }

    @PutMapping("/update")
    public Feedback update(@RequestBody Feedback f){
        return service.updateFeedback(f);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id){
        service.deleteFeedback(id);
    }

    @GetMapping("/{id}")
    public Feedback getById(@PathVariable Long id){
        return service.getById(id);
    }

    @GetMapping("/all")
    public List<Feedback> getAll(){
        return service.getAll();
    }
}