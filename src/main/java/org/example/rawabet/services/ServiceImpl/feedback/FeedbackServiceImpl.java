package org.example.rawabet.services.ServiceImpl.feedback;

import org.example.rawabet.entities.Feedback;
import org.example.rawabet.repositories.FeedbackRepository;
import org.example.rawabet.services.IService.feedback.IFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackServiceImpl implements IFeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public Feedback addFeedback(Feedback feedback) {
        feedback.setDate(java.time.LocalDate.now()); // 🔥 auto date
        return feedbackRepository.save(feedback);
    }

    @Override
    public Feedback updateFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    @Override
    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }

    @Override
    public Feedback getById(Long id) {
        return feedbackRepository.findById(id).orElse(null);
    }

    @Override
    public List<Feedback> getAll() {
        return feedbackRepository.findAll();
    }
}