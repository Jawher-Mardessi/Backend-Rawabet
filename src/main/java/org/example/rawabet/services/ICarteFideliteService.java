package org.example.rawabet.services;

import org.example.rawabet.dto.CarteFideliteResponse;
import org.example.rawabet.entities.User;
import org.example.rawabet.enums.ActionType;

public interface ICarteFideliteService {

    // 🔐 utilisateur connecté
    CarteFideliteResponse getMyCarte();

    // 🔥 logique métier interne
    void addPoints(User user, int points, ActionType action);
}