package org.example.rawabet.services;

import org.example.rawabet.dto.CarteFideliteResponse;
import org.example.rawabet.dto.FidelityHistoryResponse;
import org.example.rawabet.entities.User;
import org.example.rawabet.enums.ActionType;

import java.util.List;

public interface ICarteFideliteService {

    // 🔐 CLIENT — voir sa carte
    CarteFideliteResponse getMyCarte();

    // 🔐 ADMIN — voir carte d'un user
    CarteFideliteResponse getCarteByUser(User user);

    // 🔥 SYSTÈME — ajouter points automatiquement
    void addPoints(User user, int points, ActionType action);

    // 👑 ADMIN — ajouter points manuellement
    void addPointsByAdmin(User user, int points, ActionType action);

    // 📋 CLIENT — voir son historique
    List<FidelityHistoryResponse> getMyHistory();
}