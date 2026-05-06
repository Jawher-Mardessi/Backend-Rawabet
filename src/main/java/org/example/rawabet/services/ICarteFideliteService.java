package org.example.rawabet.services;

import org.example.rawabet.dto.*;
import org.example.rawabet.entities.User;
import org.example.rawabet.enums.ActionType;
import org.example.rawabet.enums.RewardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICarteFideliteService {

    // 🔐 CLIENT — voir sa carte
    CarteFideliteResponse getMyCarte();

    // 🔐 ADMIN — voir carte d'un user
    CarteFideliteResponse getCarteByUser(User user);

    // 🔥 SYSTÈME — ajouter points automatiquement
    void addPoints(User user, int points, ActionType action);

    // 👑 ADMIN — ajouter points manuellement
    void addPointsByAdmin(Long userId, int points, ActionType action);

    // 📋 CLIENT — voir son historique paginé
    // CORRECTION — Page au lieu de List
    Page<FidelityHistoryResponse> getMyHistory(Pageable pageable);

    LoyaltyDashboardResponse getDashboard();

    RewardResponse redeemReward(RewardType reward);
    List<RewardType> getAvailableRewards();

    // 📊 ADMIN — stats globales
    CarteStatsResponse getStats();

    LoyaltyAdminOverviewResponse getAdminOverview();

    // 🏆 ADMIN — top clients
    List<TopClientResponse> getTopClients();
    List<TopClientResponse> getTopClients(int limit);

    List<TransferRecipientResponse> searchTransferRecipients(String query);

    void transferPoints(Long toUserId, int points);
}