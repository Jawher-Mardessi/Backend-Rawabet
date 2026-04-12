package org.example.rawabet.services;

import org.example.rawabet.dto.*;
import org.example.rawabet.entities.User;
import org.example.rawabet.enums.ActionType;
import org.example.rawabet.enums.RewardType;

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

    LoyaltyDashboardResponse getDashboard();

    RewardResponse redeemReward(RewardType reward);
    List<RewardType> getAvailableRewards();

    // 📊 ADMIN — stats globales
    CarteStatsResponse getStats();

    LoyaltyAdminOverviewResponse getAdminOverview();

    // 🏆 ADMIN — top 10 clients
    List<TopClientResponse> getTopClients();
    List<TopClientResponse> getTopClients(int limit);

    void transferPoints(Long toUserId, int points);

}