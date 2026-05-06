package org.example.rawabet.services;

import org.example.rawabet.dto.SubscribeResponse;
import org.example.rawabet.dto.SubscriptionDto;
import org.example.rawabet.dto.TimelineResponse;
import org.example.rawabet.entities.Abonnement;
import org.example.rawabet.entities.QRCode;
import org.example.rawabet.entities.User;
import org.example.rawabet.entities.UserAbonnement;
import org.example.rawabet.enums.AbonnementType;
import org.example.rawabet.enums.SubscriptionStatus;
import org.example.rawabet.repositories.AbonnementRepository;
import org.example.rawabet.repositories.QRCodeRepository;
import org.example.rawabet.repositories.UserAbonnementRepository;
import org.example.rawabet.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class AbonnementServiceIntegrationTest {

    @Autowired
    private AbonnementServiceImpl abonnementService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AbonnementRepository abonnementRepository;

    @Autowired
    private UserAbonnementRepository userAbonnementRepository;

    @Autowired
    private QRCodeRepository qrCodeRepository;

    @Autowired
    private ICarteFideliteService carteFideliteService;

    private User testUser;
    private Abonnement premiumAbonnement;
    private Abonnement standardAbonnement;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setNom("Test User");
        testUser.setEmail("test@example.com");
        userRepository.save(testUser);

        // Create test abonnements
        premiumAbonnement = new Abonnement();
        premiumAbonnement.setType(AbonnementType.Premium);
        premiumAbonnement.setNom("Premium");
        premiumAbonnement.setNbTicketsParMois(5);
        premiumAbonnement.setIllimite(false);
        premiumAbonnement.setPopcornGratuit(true);
        premiumAbonnement.setPrix(50);
        abonnementRepository.save(premiumAbonnement);

        standardAbonnement = new Abonnement();
        standardAbonnement.setType(AbonnementType.Standard);
        standardAbonnement.setNom("Standard");
        standardAbonnement.setNbTicketsParMois(2);
        standardAbonnement.setIllimite(false);
        standardAbonnement.setPopcornGratuit(true);
        standardAbonnement.setPrix(20);
        abonnementRepository.save(standardAbonnement);
    }

    /**
     * Test 1: Subscribe with no existing subscription => ACTIVATED_NOW
     */
    @Test
    void testSubscribeWithNoExistingSubscription_ShouldActivateNow() {
        SubscribeResponse response = abonnementService.subscribe(testUser.getId(), premiumAbonnement.getId());

        assertNotNull(response);
        assertEquals("ACTIVATED_NOW", response.getResultType());
        assertEquals(SubscriptionStatus.ACTIVE, response.getStatus());
        assertEquals(LocalDate.now(), response.getDateDebut());
        assertEquals(LocalDate.now().plusMonths(1).minusDays(1), response.getDateFin());
        assertEquals(5, response.getTicketsRestants());
    }

    /**
     * Test 2: Subscribe when active exists => QUEUED_NEXT starting day after current end
     */
    @Test
    void testSubscribeWithActiveSubscription_ShouldQueueNext() {
        // First subscribe (activated now)
        SubscribeResponse firstResponse = abonnementService.subscribe(testUser.getId(), premiumAbonnement.getId());

        // Second subscribe (should be queued)
        SubscribeResponse secondResponse = abonnementService.subscribe(testUser.getId(), standardAbonnement.getId());

        assertNotNull(secondResponse);
        assertEquals("QUEUED_NEXT", secondResponse.getResultType());
        assertEquals(SubscriptionStatus.QUEUED, secondResponse.getStatus());

        // Second subscription should start the day after first one ends
        LocalDate expectedStart = firstResponse.getDateFin().plusDays(1);
        assertEquals(expectedStart, secondResponse.getDateDebut());
        assertEquals(expectedStart.plusMonths(1).minusDays(1), secondResponse.getDateFin());
    }

    /**
     * Test 3: Multiple queued purchases produce continuous non-overlapping periods
     */
    @Test
    void testMultipleQueuedSubscriptions_ShouldBeNonOverlapping() {
        SubscribeResponse first = abonnementService.subscribe(testUser.getId(), premiumAbonnement.getId());
        SubscribeResponse second = abonnementService.subscribe(testUser.getId(), standardAbonnement.getId());
        SubscribeResponse third = abonnementService.subscribe(testUser.getId(), premiumAbonnement.getId());

        assertEquals("ACTIVATED_NOW", first.getResultType());
        assertEquals("QUEUED_NEXT", second.getResultType());
        assertEquals("QUEUED_NEXT", third.getResultType());

        // Verify no overlap
        assertTrue(first.getDateFin().isBefore(second.getDateDebut()) ||
                first.getDateFin().isEqual(second.getDateDebut().minusDays(1)));

        assertTrue(second.getDateFin().isBefore(third.getDateDebut()) ||
                second.getDateFin().isEqual(third.getDateDebut().minusDays(1)));

        // Verify continuous sequence
        assertEquals(second.getDateDebut(), first.getDateFin().plusDays(1));
        assertEquals(third.getDateDebut(), second.getDateFin().plusDays(1));
    }

    /**
     * Test 4: Timeline endpoint returns correct current/next/queued
     */
    @Test
    void testTimelineEndpoint_ShouldReturnStructuredTimeline() {
        // Create multiple subscriptions
        abonnementService.subscribe(testUser.getId(), premiumAbonnement.getId());
        abonnementService.subscribe(testUser.getId(), standardAbonnement.getId());
        abonnementService.subscribe(testUser.getId(), premiumAbonnement.getId());

        TimelineResponse timeline = abonnementService.getTimelineForUser(testUser.getId());

        assertNotNull(timeline);
        assertNotNull(timeline.getCurrentSubscription());
        assertEquals(SubscriptionStatus.ACTIVE, timeline.getCurrentSubscription().getStatus());

        assertNotNull(timeline.getNextSubscription());
        assertEquals(SubscriptionStatus.QUEUED, timeline.getNextSubscription().getStatus());

        assertEquals(2, timeline.getQueuedSubscriptions().size());
        assertTrue(timeline.getQueuedSubscriptions().stream()
                .allMatch(s -> s.getStatus() == SubscriptionStatus.QUEUED));

        // Verify sorting of queued (should be by dateDebut ascending)
        for (int i = 0; i < timeline.getQueuedSubscriptions().size() - 1; i++) {
            assertTrue(timeline.getQueuedSubscriptions().get(i).getDateDebut()
                    .isBefore(timeline.getQueuedSubscriptions().get(i + 1).getDateDebut()));
        }
    }

    /**
     * Test 5: Expired subscriptions are preserved in history
     */
    @Test
    void testExpiredSubscriptions_ShouldBePreservedInHistory() {
        // Create subscription that's already expired
        UserAbonnement expiredSubscription = new UserAbonnement();
        expiredSubscription.setUser(testUser);
        expiredSubscription.setAbonnement(premiumAbonnement);
        expiredSubscription.setDateDebut(LocalDate.now().minusDays(60));
        expiredSubscription.setDateFin(LocalDate.now().minusDays(30));
        expiredSubscription.setTicketsRestants(3);
        expiredSubscription.setStatus(SubscriptionStatus.EXPIRED);
        userAbonnementRepository.save(expiredSubscription);

        // Create current subscription
        abonnementService.subscribe(testUser.getId(), standardAbonnement.getId());

        // Get timeline
        TimelineResponse timeline = abonnementService.getTimelineForUser(testUser.getId());

        assertNotNull(timeline.getHistory());
        assertTrue(timeline.getHistory().size() > 0);
        assertTrue(timeline.getHistory().stream()
                .anyMatch(s -> s.getStatus() == SubscriptionStatus.EXPIRED &&
                        s.getDateDebut().isEqual(LocalDate.now().minusDays(60))));
    }

    /**
     * Test 6: QR/scan with queued subscription returns expected error
     */
    @Test
    void testScanQRWithQueuedSubscription_ShouldReturnError() {
        // Create active subscription
        SubscribeResponse firstResponse = abonnementService.subscribe(testUser.getId(), premiumAbonnement.getId());

        // Create queued subscription
        SubscribeResponse secondResponse = abonnementService.subscribe(testUser.getId(), standardAbonnement.getId());

        // Get QR code from the first (active) subscription
        String qrCode = abonnementService.getQRCodeByUserId(testUser.getId());

        // Scan should work on active subscription
        AbonnementServiceImpl.ScanQRResponse scanResponse = abonnementService.scanQRCode(qrCode);
        assertNotNull(scanResponse);

        // Now manipulate to get QR code from queued subscription and try to scan
        UserAbonnement queuedSubscription = userAbonnementRepository.findByUserIdOrderByDateDebutAsc(testUser.getId())
                .stream()
                .filter(ua -> ua.getStatus() == SubscriptionStatus.QUEUED)
                .findFirst()
                .orElse(null);

        assertNotNull(queuedSubscription);
        QRCode queuedQrCode = qrCodeRepository.findByUserAbonnementId(queuedSubscription.getId()).orElse(null);

        if (queuedQrCode != null) {
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> abonnementService.scanQRCode(queuedQrCode.getCode()));
            assertTrue(exception.getMessage().contains("not active yet") ||
                    exception.getMessage().contains("not active"));
        }
    }

    /**
     * Test 7: Compute status correctly transitions subscriptions
     */
    @Test
    void testComputeStatus_ShouldTransitionCorrectly() {
        // Create active subscription
        UserAbonnement subscription = new UserAbonnement();
        subscription.setUser(testUser);
        subscription.setAbonnement(premiumAbonnement);
        subscription.setDateDebut(LocalDate.now());
        subscription.setDateFin(LocalDate.now().plusMonths(1));
        subscription.setTicketsRestants(5);
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        SubscriptionStatus status = abonnementService.computeStatus(subscription);
        assertEquals(SubscriptionStatus.ACTIVE, status);

        // Change to future (QUEUED)
        subscription.setDateDebut(LocalDate.now().plusDays(5));
        subscription.setDateFin(LocalDate.now().plusMonths(1).plusDays(5));
        status = abonnementService.computeStatus(subscription);
        assertEquals(SubscriptionStatus.QUEUED, status);

        // Change to past (EXPIRED)
        subscription.setDateDebut(LocalDate.now().minusDays(60));
        subscription.setDateFin(LocalDate.now().minusDays(30));
        status = abonnementService.computeStatus(subscription);
        assertEquals(SubscriptionStatus.EXPIRED, status);

        // Change to exhausted (0 tickets, current date)
        subscription.setDateDebut(LocalDate.now());
        subscription.setDateFin(LocalDate.now().plusMonths(1));
        subscription.setTicketsRestants(0);
        status = abonnementService.computeStatus(subscription);
        assertEquals(SubscriptionStatus.EXHAUSTED, status);
    }

    /**
     * Test 8: QR code generation on subscribe
     */
    @Test
    void testQRCodeGeneration_ShouldCreateCodeOnSubscribe() {
        SubscribeResponse response = abonnementService.subscribe(testUser.getId(), premiumAbonnement.getId());

        // Verify QR code was created
        QRCode qrCode = qrCodeRepository.findByUserAbonnementId(response.getSubscriptionId()).orElse(null);
        assertNotNull(qrCode);
        assertNotNull(qrCode.getCode());
        assertFalse(qrCode.isUsed());
        assertNull(qrCode.getScannedAt());
    }

    /**
     * Test 9: Ticket decrement on scan
     */
    @Test
    void testTicketDecrement_ShouldDecreaseOnScan() {
        SubscribeResponse response = abonnementService.subscribe(testUser.getId(), premiumAbonnement.getId());
        int initialTickets = response.getTicketsRestants();

        String qrCode = abonnementService.getQRCodeByUserId(testUser.getId());
        AbonnementServiceImpl.ScanQRResponse scanResponse = abonnementService.scanQRCode(qrCode);

        assertEquals(initialTickets - 1, scanResponse.getTicketsRemaining());
    }

    /**
     * Test 10: User subscriptions endpoint returns all subscriptions
     */
    @Test
    void testGetUserSubscriptions_ShouldReturnAllSubscriptionsIncludingQueued() {
        abonnementService.subscribe(testUser.getId(), premiumAbonnement.getId());
        abonnementService.subscribe(testUser.getId(), standardAbonnement.getId());
        abonnementService.subscribe(testUser.getId(), premiumAbonnement.getId());

        List<SubscriptionDto> subscriptions = abonnementService.getUserSubscriptions(testUser.getId());

        assertEquals(3, subscriptions.size());
        assertTrue(subscriptions.stream()
                .allMatch(s -> s.getSubscriptionId() != null && s.getAbonnementId() != null));
        assertTrue(subscriptions.stream()
                .allMatch(s -> s.getQrCode() != null && !s.getQrCode().isBlank()));
    }

    @Test
    void testGetAbonnementsByUserId_ShouldReturnPlainAbonnementList() {
        abonnementService.subscribe(testUser.getId(), premiumAbonnement.getId());
        abonnementService.subscribe(testUser.getId(), standardAbonnement.getId());

        List<Abonnement> abonnements = abonnementService.getAbonnementsByUserId(testUser.getId());

        assertEquals(2, abonnements.size());
        assertEquals(premiumAbonnement.getId(), abonnements.get(0).getId());
        assertEquals(standardAbonnement.getId(), abonnements.get(1).getId());
    }

    @Test
    void testDeleteSubscriptionById_ShouldDeleteSubscriptionAndQrCode() {
        SubscribeResponse response = abonnementService.subscribe(testUser.getId(), premiumAbonnement.getId());

        assertTrue(userAbonnementRepository.findById(response.getSubscriptionId()).isPresent());
        assertTrue(qrCodeRepository.findByUserAbonnementId(response.getSubscriptionId()).isPresent());

        abonnementService.deleteSubscriptionById(response.getSubscriptionId());

        assertTrue(userAbonnementRepository.findById(response.getSubscriptionId()).isEmpty());
        assertTrue(qrCodeRepository.findByUserAbonnementId(response.getSubscriptionId()).isEmpty());
    }
}
