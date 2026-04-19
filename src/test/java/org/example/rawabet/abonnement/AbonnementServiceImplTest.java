package org.example.rawabet.abonnement;

import org.example.rawabet.dto.abonnement.SubscribeResponseDTO;
import org.example.rawabet.dto.abonnement.SubscriptionTimelineDTO;
import org.example.rawabet.dto.abonnement.UserAbonnementDTO;
import org.example.rawabet.entities.Abonnement;
import org.example.rawabet.entities.QRCode;
import org.example.rawabet.entities.User;
import org.example.rawabet.entities.UserAbonnement;
import org.example.rawabet.enums.AbonnementType;
import org.example.rawabet.enums.SubscribeResultType;
import org.example.rawabet.enums.UserAbonnementStatus;
import org.example.rawabet.repositories.AbonnementRepository;
import org.example.rawabet.repositories.QRCodeRepository;
import org.example.rawabet.repositories.UserAbonnementRepository;
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.services.AbonnementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbonnementServiceImplTest {

    @Mock private AbonnementRepository abonnementRepository;
    @Mock private UserAbonnementRepository userAbonnementRepository;
    @Mock private QRCodeRepository qrCodeRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private AbonnementServiceImpl service;

    private User user;
    private Abonnement plan;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setNom("Test User");
        user.setEmail("test@test.com");

        plan = Abonnement.builder()
                .id(10L)
                .type(AbonnementType.MENSUEL)
                .prix(29.99)
                .dureeEnMois(1)
                .ticketsMax(10)
                .description("Mensuel 10 tickets")
                .build();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // subscribe — no existing subscription → ACTIVATED_NOW
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void subscribe_withNoExistingSubscription_shouldReturnActivatedNow() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(abonnementRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(userAbonnementRepository.findByUserForUpdate(user)).thenReturn(new ArrayList<>());

        ArgumentCaptor<UserAbonnement> uaCaptor = ArgumentCaptor.forClass(UserAbonnement.class);
        when(userAbonnementRepository.save(uaCaptor.capture())).thenAnswer(inv -> {
            UserAbonnement ua = inv.getArgument(0);
            ua = UserAbonnement.builder()
                    .id(100L)
                    .user(ua.getUser())
                    .abonnement(ua.getAbonnement())
                    .dateDebut(ua.getDateDebut())
                    .dateFin(ua.getDateFin())
                    .status(ua.getStatus())
                    .ticketsRestants(ua.getTicketsRestants())
                    .build();
            return ua;
        });
        when(qrCodeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SubscribeResponseDTO result = service.subscribe(1L, 10L);

        assertThat(result.getResultType()).isEqualTo(SubscribeResultType.ACTIVATED_NOW);
        assertThat(result.getStatus()).isEqualTo(UserAbonnementStatus.ACTIVE);
        assertThat(result.getDateDebut()).isEqualTo(LocalDate.now());
        assertThat(result.getDateFin()).isEqualTo(LocalDate.now().plusMonths(1).minusDays(1));
        assertThat(result.getTicketsRestants()).isEqualTo(10);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // subscribe — active subscription exists → QUEUED_NEXT
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void subscribe_withActiveSubscription_shouldReturnQueuedNext() {
        LocalDate today = LocalDate.now();
        LocalDate existingEnd = today.plusDays(15);

        UserAbonnement existingActive = UserAbonnement.builder()
                .id(50L)
                .user(user)
                .abonnement(plan)
                .dateDebut(today.minusDays(5))
                .dateFin(existingEnd)
                .status(UserAbonnementStatus.ACTIVE)
                .ticketsRestants(8)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(abonnementRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(userAbonnementRepository.findByUserForUpdate(user))
                .thenReturn(new ArrayList<>(List.of(existingActive)));
        when(userAbonnementRepository.save(any())).thenAnswer(inv -> {
            UserAbonnement ua = inv.getArgument(0);
            if (ua.getId() == null) {
                ua = UserAbonnement.builder()
                        .id(101L)
                        .user(ua.getUser())
                        .abonnement(ua.getAbonnement())
                        .dateDebut(ua.getDateDebut())
                        .dateFin(ua.getDateFin())
                        .status(ua.getStatus())
                        .ticketsRestants(ua.getTicketsRestants())
                        .build();
            }
            return ua;
        });

        SubscribeResponseDTO result = service.subscribe(1L, 10L);

        assertThat(result.getResultType()).isEqualTo(SubscribeResultType.QUEUED_NEXT);
        assertThat(result.getStatus()).isEqualTo(UserAbonnementStatus.QUEUED);
        assertThat(result.getDateDebut()).isEqualTo(existingEnd.plusDays(1));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // subscribe — multiple queued produce continuous non-overlapping periods
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void subscribe_multipleQueued_producesContinuousNonOverlappingPeriods() {
        LocalDate today = LocalDate.now();
        LocalDate firstEnd = today.plusDays(10);
        LocalDate secondEnd = firstEnd.plusMonths(1).minusDays(1);

        UserAbonnement first = UserAbonnement.builder()
                .id(50L).user(user).abonnement(plan)
                .dateDebut(today).dateFin(firstEnd)
                .status(UserAbonnementStatus.ACTIVE).ticketsRestants(10).build();

        UserAbonnement second = UserAbonnement.builder()
                .id(51L).user(user).abonnement(plan)
                .dateDebut(firstEnd.plusDays(1)).dateFin(secondEnd)
                .status(UserAbonnementStatus.QUEUED).ticketsRestants(10).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(abonnementRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(userAbonnementRepository.findByUserForUpdate(user))
                .thenReturn(new ArrayList<>(List.of(first, second)));
        when(userAbonnementRepository.save(any())).thenAnswer(inv -> {
            UserAbonnement ua = inv.getArgument(0);
            if (ua.getId() == null) {
                ua = UserAbonnement.builder()
                        .id(102L).user(ua.getUser()).abonnement(ua.getAbonnement())
                        .dateDebut(ua.getDateDebut()).dateFin(ua.getDateFin())
                        .status(ua.getStatus()).ticketsRestants(ua.getTicketsRestants()).build();
            }
            return ua;
        });

        SubscribeResponseDTO result = service.subscribe(1L, 10L);

        assertThat(result.getResultType()).isEqualTo(SubscribeResultType.QUEUED_NEXT);
        // Must start the day after the last queued end date (secondEnd)
        assertThat(result.getDateDebut()).isEqualTo(secondEnd.plusDays(1));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // getTimeline — returns correct current/next/queued
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void getTimeline_returnsCorrectCurrentAndNextAndQueued() {
        LocalDate today = LocalDate.now();

        UserAbonnement active = UserAbonnement.builder()
                .id(1L).user(user).abonnement(plan)
                .dateDebut(today.minusDays(5)).dateFin(today.plusDays(25))
                .status(UserAbonnementStatus.ACTIVE).ticketsRestants(8).build();

        UserAbonnement queued1 = UserAbonnement.builder()
                .id(2L).user(user).abonnement(plan)
                .dateDebut(today.plusDays(26)).dateFin(today.plusDays(56))
                .status(UserAbonnementStatus.QUEUED).ticketsRestants(10).build();

        UserAbonnement queued2 = UserAbonnement.builder()
                .id(3L).user(user).abonnement(plan)
                .dateDebut(today.plusDays(57)).dateFin(today.plusDays(87))
                .status(UserAbonnementStatus.QUEUED).ticketsRestants(10).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userAbonnementRepository.findByUserOrderByDateDebutAsc(user))
                .thenReturn(List.of(active, queued1, queued2));

        SubscriptionTimelineDTO timeline = service.getTimeline(1L);

        assertThat(timeline.getCurrentSubscription()).isNotNull();
        assertThat(timeline.getCurrentSubscription().getId()).isEqualTo(1L);
        assertThat(timeline.getNextSubscription()).isNotNull();
        assertThat(timeline.getNextSubscription().getId()).isEqualTo(2L);
        assertThat(timeline.getQueuedSubscriptions()).hasSize(2);
        assertThat(timeline.getHistory()).isEmpty();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // getTimeline — expired subscriptions are preserved in history
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void getTimeline_expiredSubscriptionsPreservedInHistory() {
        LocalDate today = LocalDate.now();

        UserAbonnement expired = UserAbonnement.builder()
                .id(1L).user(user).abonnement(plan)
                .dateDebut(today.minusDays(60)).dateFin(today.minusDays(30))
                .status(UserAbonnementStatus.EXPIRED).ticketsRestants(0).build();

        UserAbonnement active = UserAbonnement.builder()
                .id(2L).user(user).abonnement(plan)
                .dateDebut(today.minusDays(10)).dateFin(today.plusDays(20))
                .status(UserAbonnementStatus.ACTIVE).ticketsRestants(5).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userAbonnementRepository.findByUserOrderByDateDebutAsc(user))
                .thenReturn(List.of(expired, active));

        SubscriptionTimelineDTO timeline = service.getTimeline(1L);

        assertThat(timeline.getCurrentSubscription()).isNotNull();
        assertThat(timeline.getHistory()).hasSize(1);
        assertThat(timeline.getHistory().get(0).getId()).isEqualTo(1L);
        assertThat(timeline.getHistory().get(0).getStatus()).isEqualTo(UserAbonnementStatus.EXPIRED);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // scanQRCode — queued subscription returns expected error
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void scanQRCode_queuedSubscription_throwsExpectedError() {
        LocalDate future = LocalDate.now().plusDays(5);

        UserAbonnement queued = UserAbonnement.builder()
                .id(1L).user(user).abonnement(plan)
                .dateDebut(future).dateFin(future.plusMonths(1))
                .status(UserAbonnementStatus.QUEUED).ticketsRestants(10).build();

        QRCode qrCode = QRCode.builder()
                .id(1L).code("test-code").userAbonnement(queued)
                .generatedAt(java.time.LocalDateTime.now()).build();

        when(qrCodeRepository.findByCode("test-code")).thenReturn(Optional.of(qrCode));

        assertThatThrownBy(() -> service.scanQRCode("test-code"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("pas encore actif")
                .hasMessageContaining(future.toString());
    }

    // ══════════════════════════════════════════════════════════════════════════
    // computeStatus — unit tests for the status computation logic
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void computeStatus_activeSubscription_returnsActive() {
        LocalDate today = LocalDate.now();
        UserAbonnement ua = UserAbonnement.builder()
                .abonnement(plan)
                .dateDebut(today.minusDays(5))
                .dateFin(today.plusDays(25))
                .status(UserAbonnementStatus.ACTIVE)
                .ticketsRestants(5)
                .build();

        assertThat(service.computeStatus(ua)).isEqualTo(UserAbonnementStatus.ACTIVE);
    }

    @Test
    void computeStatus_futureSubscription_returnsQueued() {
        LocalDate today = LocalDate.now();
        UserAbonnement ua = UserAbonnement.builder()
                .abonnement(plan)
                .dateDebut(today.plusDays(3))
                .dateFin(today.plusDays(33))
                .status(UserAbonnementStatus.QUEUED)
                .ticketsRestants(10)
                .build();

        assertThat(service.computeStatus(ua)).isEqualTo(UserAbonnementStatus.QUEUED);
    }

    @Test
    void computeStatus_pastSubscription_returnsExpired() {
        LocalDate today = LocalDate.now();
        UserAbonnement ua = UserAbonnement.builder()
                .abonnement(plan)
                .dateDebut(today.minusDays(60))
                .dateFin(today.minusDays(30))
                .status(UserAbonnementStatus.ACTIVE)
                .ticketsRestants(5)
                .build();

        assertThat(service.computeStatus(ua)).isEqualTo(UserAbonnementStatus.EXPIRED);
    }

    @Test
    void computeStatus_noTicketsLeft_returnsExhausted() {
        LocalDate today = LocalDate.now();
        UserAbonnement ua = UserAbonnement.builder()
                .abonnement(plan)
                .dateDebut(today.minusDays(5))
                .dateFin(today.plusDays(25))
                .status(UserAbonnementStatus.ACTIVE)
                .ticketsRestants(0)
                .build();

        assertThat(service.computeStatus(ua)).isEqualTo(UserAbonnementStatus.EXHAUSTED);
    }

    @Test
    void computeStatus_unlimitedPlanNoTickets_returnsActive() {
        LocalDate today = LocalDate.now();
        Abonnement unlimited = Abonnement.builder()
                .id(20L).type(AbonnementType.PREMIUM)
                .dureeEnMois(1).ticketsMax(-1).build();

        UserAbonnement ua = UserAbonnement.builder()
                .abonnement(unlimited)
                .dateDebut(today.minusDays(5))
                .dateFin(today.plusDays(25))
                .status(UserAbonnementStatus.ACTIVE)
                .ticketsRestants(-1)
                .build();

        assertThat(service.computeStatus(ua)).isEqualTo(UserAbonnementStatus.ACTIVE);
    }
}
