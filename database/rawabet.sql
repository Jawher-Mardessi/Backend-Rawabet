-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : jeu. 14 mai 2026 à 23:01
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `rawabet`
--

-- --------------------------------------------------------

--
-- Structure de la table `abonnement`
--

CREATE TABLE `abonnement` (
  `id` bigint(20) NOT NULL,
  `illimite` bit(1) NOT NULL,
  `nb_tickets_par_mois` int(11) NOT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `popcorn_gratuit` bit(1) NOT NULL,
  `prix` double NOT NULL,
  `type` enum('Premium','Standard','VIP') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `abonnement`
--

INSERT INTO `abonnement` (`id`, `illimite`, `nb_tickets_par_mois`, `nom`, `popcorn_gratuit`, `prix`, `type`) VALUES
(1, b'1', 0, 'VIP', b'1', 100, 'VIP'),
(2, b'0', 5, 'Premium', b'1', 50, 'Premium'),
(3, b'0', 2, 'Standard', b'1', 20, 'Standard');

-- --------------------------------------------------------

--
-- Structure de la table `activity_log`
--

CREATE TABLE `activity_log` (
  `id` bigint(20) NOT NULL,
  `color` varchar(255) NOT NULL,
  `detail` varchar(255) DEFAULT NULL,
  `icon` varchar(10) NOT NULL,
  `message` varchar(255) NOT NULL,
  `timestamp` datetime(6) NOT NULL,
  `type` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `carte_fidelite`
--

CREATE TABLE `carte_fidelite` (
  `id` bigint(20) NOT NULL,
  `date_expiration` date NOT NULL,
  `level` enum('GOLD','SILVER','VIP') NOT NULL,
  `points` int(11) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `carte_fidelite`
--

INSERT INTO `carte_fidelite` (`id`, `date_expiration`, `level`, `points`, `user_id`) VALUES
(1, '2027-05-14', 'SILVER', 0, 1);

-- --------------------------------------------------------

--
-- Structure de la table `categorie_materiel`
--

CREATE TABLE `categorie_materiel` (
  `id` bigint(20) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `nom` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `chat_session`
--

CREATE TABLE `chat_session` (
  `id` bigint(20) NOT NULL,
  `active` bit(1) NOT NULL,
  `code` varchar(255) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `end_time` datetime(6) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `seance_id` bigint(20) NOT NULL,
  `start_time` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `cinemas`
--

CREATE TABLE `cinemas` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `opening_hours` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `slug` varchar(255) NOT NULL,
  `timezone` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `club`
--

CREATE TABLE `club` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `club_event`
--

CREATE TABLE `club_event` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(2000) DEFAULT NULL,
  `event_date` datetime(6) NOT NULL,
  `max_places` int(11) NOT NULL,
  `poster_url` varchar(255) DEFAULT NULL,
  `reserved_places` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `club_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `club_join_request`
--

CREATE TABLE `club_join_request` (
  `id` bigint(20) NOT NULL,
  `motivation` varchar(1000) DEFAULT NULL,
  `processed_date` datetime(6) DEFAULT NULL,
  `request_date` datetime(6) DEFAULT NULL,
  `status` enum('APPROVED','PENDING','REJECTED') DEFAULT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `club_member`
--

CREATE TABLE `club_member` (
  `id` bigint(20) NOT NULL,
  `joined_at` datetime(6) DEFAULT NULL,
  `remove_reason` varchar(500) DEFAULT NULL,
  `removed_at` datetime(6) DEFAULT NULL,
  `status` enum('ACTIVE','LEFT','REMOVED') DEFAULT NULL,
  `club_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `club_participation`
--

CREATE TABLE `club_participation` (
  `id` bigint(20) NOT NULL,
  `reservation_date` datetime(6) DEFAULT NULL,
  `reserved_places` int(11) NOT NULL,
  `status` enum('CANCELLED','CONFIRMED') DEFAULT NULL,
  `club_event_id` bigint(20) NOT NULL,
  `club_member_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `email_verification_token`
--

CREATE TABLE `email_verification_token` (
  `id` bigint(20) NOT NULL,
  `expires_at` datetime(6) NOT NULL,
  `token` varchar(255) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `evenement`
--

CREATE TABLE `evenement` (
  `id` bigint(20) NOT NULL,
  `date_debut` datetime(6) DEFAULT NULL,
  `date_fin` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `nombre_de_places` int(11) NOT NULL,
  `status` enum('CANCELLED','DRAFT','PUBLISHED') DEFAULT NULL,
  `titre` varchar(255) DEFAULT NULL,
  `salle_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `evenement_materiel`
--

CREATE TABLE `evenement_materiel` (
  `id` bigint(20) NOT NULL,
  `quantite` int(11) NOT NULL,
  `evenement_id` bigint(20) DEFAULT NULL,
  `materiel_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `facture`
--

CREATE TABLE `facture` (
  `id` bigint(20) NOT NULL,
  `date_emission` date DEFAULT NULL,
  `montant` double NOT NULL,
  `numero_facture` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `feedback`
--

CREATE TABLE `feedback` (
  `id` bigint(20) NOT NULL,
  `commentaire` varchar(255) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `note` int(11) NOT NULL,
  `film_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `fidelity_history`
--

CREATE TABLE `fidelity_history` (
  `id` bigint(20) NOT NULL,
  `action` enum('BONUS','CINEMA','CLUB','EVENT','REWARD_REDEEMED','TRANSFER_IN','TRANSFER_OUT') DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `points` int(11) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `films`
--

CREATE TABLE `films` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `average_rating` float DEFAULT NULL,
  `cast_summary` varchar(255) DEFAULT NULL,
  `director` varchar(255) DEFAULT NULL,
  `duration_minutes` int(11) DEFAULT NULL,
  `genre` varchar(255) DEFAULT NULL,
  `imdb_id` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `language` varchar(255) DEFAULT NULL,
  `poster_url` varchar(255) DEFAULT NULL,
  `profitable` bit(1) DEFAULT NULL,
  `rating` varchar(255) DEFAULT NULL,
  `release_date` date DEFAULT NULL,
  `roi_confidence` double DEFAULT NULL,
  `roi_label` varchar(255) DEFAULT NULL,
  `synopsis` varchar(2000) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `total_reviews` int(11) DEFAULT NULL,
  `trailer_url` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `materiel`
--

CREATE TABLE `materiel` (
  `id` bigint(20) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `disponible` bit(1) NOT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `prix_unitaire` double NOT NULL,
  `quantite_disponible` int(11) NOT NULL,
  `reference` varchar(255) DEFAULT NULL,
  `status` enum('ACTIVE','DAMAGED','MAINTENANCE') DEFAULT NULL,
  `categorie_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `message`
--

CREATE TABLE `message` (
  `id` bigint(20) NOT NULL,
  `chat_session_id` bigint(20) NOT NULL,
  `content` text NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  `edited` tinyint(1) NOT NULL DEFAULT 0,
  `edited_at` datetime(6) DEFAULT NULL,
  `spoiler` tinyint(1) NOT NULL DEFAULT 0,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `message_hidden`
--

CREATE TABLE `message_hidden` (
  `id` bigint(20) NOT NULL,
  `message_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `message_reaction`
--

CREATE TABLE `message_reaction` (
  `id` bigint(20) NOT NULL,
  `chat_session_id` bigint(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `emoji` varchar(10) NOT NULL,
  `message_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `notification`
--

CREATE TABLE `notification` (
  `id` bigint(20) NOT NULL,
  `date_envoi` datetime(6) DEFAULT NULL,
  `lue` bit(1) NOT NULL,
  `message` varchar(255) DEFAULT NULL,
  `type` enum('EMAIL','PUSH','SMS') DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `paiement`
--

CREATE TABLE `paiement` (
  `id` bigint(20) NOT NULL,
  `date_paiement` date DEFAULT NULL,
  `montant` double NOT NULL,
  `statut` enum('FAILED','PENDING','SUCCESS') DEFAULT NULL,
  `facture_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `passkey_credentials`
--

CREATE TABLE `passkey_credentials` (
  `id` bigint(20) NOT NULL,
  `backed_up` bit(1) NOT NULL,
  `backup_eligible` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `credential_id` varchar(512) NOT NULL,
  `public_key_cose` text NOT NULL,
  `signature_count` bigint(20) NOT NULL,
  `transports` varchar(1000) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `user_handle` varchar(128) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `password_reset_token`
--

CREATE TABLE `password_reset_token` (
  `id` bigint(20) NOT NULL,
  `expires_at` datetime(6) NOT NULL,
  `failed_attempts` int(11) NOT NULL,
  `token` varchar(255) NOT NULL,
  `used` bit(1) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `permission`
--

CREATE TABLE `permission` (
  `id` bigint(20) NOT NULL,
  `action` varchar(255) NOT NULL,
  `module` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `permission`
--

INSERT INTO `permission` (`id`, `action`, `module`, `name`) VALUES
(1, 'MANAGE', 'ADMIN', 'ADMIN_MANAGE'),
(2, 'CREATE', 'CINEMA', 'CINEMA_CREATE'),
(3, 'READ', 'CINEMA', 'CINEMA_READ'),
(4, 'UPDATE', 'CINEMA', 'CINEMA_UPDATE'),
(5, 'DELETE', 'CINEMA', 'CINEMA_DELETE'),
(6, 'CREATE', 'FILM', 'FILM_CREATE'),
(7, 'READ', 'FILM', 'FILM_READ'),
(8, 'UPDATE', 'FILM', 'FILM_UPDATE'),
(9, 'DELETE', 'FILM', 'FILM_DELETE'),
(10, 'CREATE', 'EVENT', 'EVENT_CREATE'),
(11, 'READ', 'EVENT', 'EVENT_READ'),
(12, 'UPDATE', 'EVENT', 'EVENT_UPDATE'),
(13, 'DELETE', 'EVENT', 'EVENT_DELETE'),
(14, 'CREATE', 'CLUB', 'CLUB_CREATE'),
(15, 'READ', 'CLUB', 'CLUB_READ'),
(16, 'UPDATE', 'CLUB', 'CLUB_UPDATE'),
(17, 'DELETE', 'CLUB', 'CLUB_DELETE'),
(18, 'MANAGE', 'CLUB', 'CLUB_MANAGE'),
(19, 'READ', 'FIDELITY', 'FIDELITY_READ'),
(20, 'UPDATE', 'FIDELITY', 'FIDELITY_UPDATE');

-- --------------------------------------------------------

--
-- Structure de la table `qr_codes`
--

CREATE TABLE `qr_codes` (
  `id` bigint(20) NOT NULL,
  `code` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `scanned_at` datetime(6) DEFAULT NULL,
  `used` bit(1) NOT NULL,
  `user_abonnement_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `reservation_cinema`
--

CREATE TABLE `reservation_cinema` (
  `id` bigint(20) NOT NULL,
  `date_reservation` date DEFAULT NULL,
  `statut` enum('CANCELLED','CONFIRMED','PENDING') DEFAULT NULL,
  `paiement_id` bigint(20) DEFAULT NULL,
  `seance_id` bigint(20) DEFAULT NULL,
  `seat_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `reservation_evenement`
--

CREATE TABLE `reservation_evenement` (
  `id` bigint(20) NOT NULL,
  `date_expiration` datetime(6) DEFAULT NULL,
  `date_reservation` datetime(6) DEFAULT NULL,
  `en_attente` bit(1) NOT NULL,
  `statut` enum('CANCELLED','CONFIRMED','PENDING') DEFAULT NULL,
  `evenement_id` bigint(20) DEFAULT NULL,
  `paiement_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `reservation_materiel`
--

CREATE TABLE `reservation_materiel` (
  `id` bigint(20) NOT NULL,
  `date_debut` datetime(6) DEFAULT NULL,
  `date_fin` datetime(6) DEFAULT NULL,
  `quantite` int(11) NOT NULL,
  `statut` enum('CANCELLED','CONFIRMED','PENDING') DEFAULT NULL,
  `materiel_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `role`
--

CREATE TABLE `role` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `role`
--

INSERT INTO `role` (`id`, `name`) VALUES
(2, 'ADMIN_CINEMA'),
(4, 'ADMIN_CLUB'),
(3, 'ADMIN_EVENT'),
(5, 'CLIENT'),
(1, 'SUPER_ADMIN');

-- --------------------------------------------------------

--
-- Structure de la table `role_permissions`
--

CREATE TABLE `role_permissions` (
  `role_id` bigint(20) NOT NULL,
  `permission_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `role_permissions`
--

INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES
(1, 15),
(1, 12),
(1, 19),
(1, 8),
(1, 7),
(1, 3),
(1, 16),
(1, 18),
(1, 1),
(1, 6),
(1, 14),
(1, 17),
(1, 11),
(1, 4),
(1, 9),
(1, 5),
(1, 10),
(1, 13),
(1, 20),
(1, 2),
(2, 2),
(2, 3),
(2, 4),
(2, 5),
(3, 10),
(3, 11),
(3, 12),
(3, 13),
(4, 14),
(4, 15),
(4, 16),
(4, 17),
(4, 18),
(5, 19),
(5, 20);

-- --------------------------------------------------------

--
-- Structure de la table `salles_cinema`
--

CREATE TABLE `salles_cinema` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `hall_type` enum('PREMIUM','STANDARD') NOT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `screen_type` enum('IMAX','THREE_D','TWO_D') NOT NULL,
  `total_capacity` int(11) DEFAULT NULL,
  `cinema_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `salle_evenement`
--

CREATE TABLE `salle_evenement` (
  `id` bigint(20) NOT NULL,
  `capacite` int(11) NOT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `status` enum('ACTIVE','MAINTENANCE') DEFAULT NULL,
  `type` enum('EXTERIEUR','INTERIEUR') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `seance`
--

CREATE TABLE `seance` (
  `id` bigint(20) NOT NULL,
  `date_heure` datetime(6) DEFAULT NULL,
  `langue` varchar(255) DEFAULT NULL,
  `prix_base` double NOT NULL,
  `film_id` bigint(20) DEFAULT NULL,
  `salle_cinema_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `seats`
--

CREATE TABLE `seats` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `full_label` varchar(255) NOT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `seat_number` int(11) NOT NULL,
  `seat_type` enum('PMR','STANDARD') NOT NULL,
  `row_id` bigint(20) NOT NULL,
  `seance_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `seat_rows`
--

CREATE TABLE `seat_rows` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `display_order` int(11) NOT NULL,
  `row_label` varchar(255) NOT NULL,
  `seat_count` int(11) NOT NULL,
  `salle_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `user`
--

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  `ban_reason` varchar(500) DEFAULT NULL,
  `ban_until` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `login_failed_attempts` int(11) NOT NULL DEFAULT 0,
  `login_locked_until` datetime(6) DEFAULT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `token_version` int(11) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `user`
--

INSERT INTO `user` (`id`, `avatar_url`, `ban_reason`, `ban_until`, `created_at`, `email`, `is_active`, `login_failed_attempts`, `login_locked_until`, `nom`, `password`, `token_version`, `updated_at`) VALUES
(1, NULL, NULL, NULL, '2026-05-14 20:57:57.000000', 'admin@test.com', 1, 0, NULL, 'SuperAdmin', '$2a$10$CfikiaNCaBS9klmR5TNRpufzYqBkW0N3N.pUOYTuHwSCJvVmU755O', 0, '2026-05-14 20:57:57.000000');

-- --------------------------------------------------------

--
-- Structure de la table `user_abonnement`
--

CREATE TABLE `user_abonnement` (
  `id` bigint(20) NOT NULL,
  `date_debut` date DEFAULT NULL,
  `date_fin` date DEFAULT NULL,
  `status` enum('ACTIVE','EXHAUSTED','EXPIRED','QUEUED') DEFAULT NULL,
  `tickets_restants` int(11) NOT NULL,
  `abonnement_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `user_roles`
--

CREATE TABLE `user_roles` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `user_roles`
--

INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES
(1, 1),
(1, 5);

-- --------------------------------------------------------

--
-- Structure de la table `webauthn_ceremonies`
--

CREATE TABLE `webauthn_ceremonies` (
  `request_id` varchar(64) NOT NULL,
  `consumed` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `expires_at` datetime(6) NOT NULL,
  `purpose` enum('AUTHENTICATION','REGISTRATION') NOT NULL,
  `request_json` longtext NOT NULL,
  `user_handle` varchar(128) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `abonnement`
--
ALTER TABLE `abonnement`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `activity_log`
--
ALTER TABLE `activity_log`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_activity_timestamp` (`timestamp`),
  ADD KEY `idx_activity_type` (`type`);

--
-- Index pour la table `carte_fidelite`
--
ALTER TABLE `carte_fidelite`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKioep813bycrnc0u6euhgsyh32` (`user_id`);

--
-- Index pour la table `categorie_materiel`
--
ALTER TABLE `categorie_materiel`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `chat_session`
--
ALTER TABLE `chat_session`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK3qprlk3uuam16nxb97kfm3ok0` (`code`);

--
-- Index pour la table `cinemas`
--
ALTER TABLE `cinemas`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK284o5qk4p7p3slmm4c6rd4m5h` (`slug`);

--
-- Index pour la table `club`
--
ALTER TABLE `club`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `club_event`
--
ALTER TABLE `club_event`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKi2h9w0o5q2wxx1a6h1rgnc4bf` (`club_id`);

--
-- Index pour la table `club_join_request`
--
ALTER TABLE `club_join_request`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKo61r30ilmgmlj4bjfsojvr709` (`user_id`);

--
-- Index pour la table `club_member`
--
ALTER TABLE `club_member`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_club_member_user_club` (`user_id`,`club_id`),
  ADD KEY `FKf6tl19ih8acrmheidn4xos2tx` (`club_id`);

--
-- Index pour la table `club_participation`
--
ALTER TABLE `club_participation`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK2b98yxj39ei2xg3jaj83o68wj` (`club_member_id`,`club_event_id`),
  ADD KEY `IDXa6l1oxjx3bkdq9v24yb3qn2e4` (`club_member_id`),
  ADD KEY `IDX6t9t5mrnamb7id1h2kk3lcj4t` (`club_event_id`);

--
-- Index pour la table `email_verification_token`
--
ALTER TABLE `email_verification_token`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKidu2ippaks8bn6vcsq62khvdu` (`token`),
  ADD UNIQUE KEY `UK1sxbwflvq4skafkocq315i9dt` (`user_id`);

--
-- Index pour la table `evenement`
--
ALTER TABLE `evenement`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKx2glu4attb13wp73tbfjlc8y` (`salle_id`);

--
-- Index pour la table `evenement_materiel`
--
ALTER TABLE `evenement_materiel`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKk1f0kak46fa8qu034c875fkdh` (`evenement_id`),
  ADD KEY `FKcjk5xy95tr8cdqk4cbtbvhwe` (`materiel_id`);

--
-- Index pour la table `facture`
--
ALTER TABLE `facture`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `feedback`
--
ALTER TABLE `feedback`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKp3k1tyqn5injav3aba67tkyh0` (`film_id`),
  ADD KEY `FK7k33yw505d347mw3avr93akao` (`user_id`);

--
-- Index pour la table `fidelity_history`
--
ALTER TABLE `fidelity_history`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK99cph0erd24o4q3mx4a49l275` (`user_id`);

--
-- Index pour la table `films`
--
ALTER TABLE `films`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKfov1x9t08okeue4itq5f9fwn1` (`imdb_id`);

--
-- Index pour la table `materiel`
--
ALTER TABLE `materiel`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK9rtmkvoq46ophu2yjv6ire6qu` (`categorie_id`);

--
-- Index pour la table `message`
--
ALTER TABLE `message`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `message_hidden`
--
ALTER TABLE `message_hidden`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKls6oa0tyqeyehd9jh8llhnm1t` (`message_id`,`user_id`);

--
-- Index pour la table `message_reaction`
--
ALTER TABLE `message_reaction`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKg6itxhlrvu7v7mbjqlk56rdoy` (`message_id`,`user_id`);

--
-- Index pour la table `notification`
--
ALTER TABLE `notification`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKb0yvoep4h4k92ipon31wmdf7e` (`user_id`);

--
-- Index pour la table `paiement`
--
ALTER TABLE `paiement`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKenp43tc4e0akhf529h0of74ep` (`facture_id`);

--
-- Index pour la table `passkey_credentials`
--
ALTER TABLE `passkey_credentials`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKqenmfnuyo3mltfys67t14xel2` (`credential_id`),
  ADD KEY `FKjrgl4avsbi2hhu9qbuvyqyxkh` (`user_id`);

--
-- Index pour la table `password_reset_token`
--
ALTER TABLE `password_reset_token`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKg0guo4k8krgpwuagos61oc06j` (`token`),
  ADD UNIQUE KEY `UKf90ivichjaokvmovxpnlm5nin` (`user_id`);

--
-- Index pour la table `permission`
--
ALTER TABLE `permission`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK2ojme20jpga3r4r79tdso17gi` (`name`);

--
-- Index pour la table `qr_codes`
--
ALTER TABLE `qr_codes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKjbhqni35qt5q7o9oab1wwww34` (`code`),
  ADD UNIQUE KEY `UKammv9i94cyx521wngy8uyca9y` (`user_abonnement_id`);

--
-- Index pour la table `reservation_cinema`
--
ALTER TABLE `reservation_cinema`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK4wafvfj6ux5cx9yjpk0mur7ne` (`paiement_id`),
  ADD KEY `FKckk4uo2rq7vj3d1foqna0h0dg` (`seance_id`),
  ADD KEY `FKjghpx2douqo96r6tdk010fr0h` (`seat_id`),
  ADD KEY `FKcn8dpd7uey7owbme0w6k4lg4a` (`user_id`);

--
-- Index pour la table `reservation_evenement`
--
ALTER TABLE `reservation_evenement`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKr1jfits692mmjg1moqwvikooh` (`paiement_id`),
  ADD KEY `FKp4amyyu06w9i8j2p90xm4lhgn` (`evenement_id`),
  ADD KEY `FKfm82fyf9hj2b3rd78xyca0jhp` (`user_id`);

--
-- Index pour la table `reservation_materiel`
--
ALTER TABLE `reservation_materiel`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK320jpves31oq0ohoftvxpatni` (`materiel_id`),
  ADD KEY `FK8umxr2ubrrgj6o47gommj5m7c` (`user_id`);

--
-- Index pour la table `role`
--
ALTER TABLE `role`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK8sewwnpamngi6b1dwaa88askk` (`name`);

--
-- Index pour la table `role_permissions`
--
ALTER TABLE `role_permissions`
  ADD KEY `FKh0v7u4w7mttcu81o8wegayr8e` (`permission_id`),
  ADD KEY `FKlodb7xh4a2xjv39gc3lsop95n` (`role_id`);

--
-- Index pour la table `salles_cinema`
--
ALTER TABLE `salles_cinema`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKqyey6jbo2xvp7ih34otbh60vh` (`cinema_id`);

--
-- Index pour la table `salle_evenement`
--
ALTER TABLE `salle_evenement`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `seance`
--
ALTER TABLE `seance`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK4e8g1b9ybe9mjno91iw09o0jp` (`film_id`),
  ADD KEY `FK4bp2pfqtmghepeangytt27lnn` (`salle_cinema_id`);

--
-- Index pour la table `seats`
--
ALTER TABLE `seats`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKp0ut4wcsbu75ob0ipu541sweq` (`row_id`,`full_label`),
  ADD KEY `FKdq696jb1s2wj0j7toyis3dqy2` (`seance_id`);

--
-- Index pour la table `seat_rows`
--
ALTER TABLE `seat_rows`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_seatrow_salle_label` (`salle_id`,`row_label`);

--
-- Index pour la table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`);

--
-- Index pour la table `user_abonnement`
--
ALTER TABLE `user_abonnement`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKjm9ges5fmbcaym63yf8fmingq` (`abonnement_id`),
  ADD KEY `FKjg3ak49t7qwpbux5mykhq9jq2` (`user_id`);

--
-- Index pour la table `user_roles`
--
ALTER TABLE `user_roles`
  ADD KEY `FKrhfovtciq1l558cw6udg0h0d3` (`role_id`),
  ADD KEY `FK55itppkw3i07do3h7qoclqd4k` (`user_id`);

--
-- Index pour la table `webauthn_ceremonies`
--
ALTER TABLE `webauthn_ceremonies`
  ADD PRIMARY KEY (`request_id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `abonnement`
--
ALTER TABLE `abonnement`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `activity_log`
--
ALTER TABLE `activity_log`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `carte_fidelite`
--
ALTER TABLE `carte_fidelite`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT pour la table `categorie_materiel`
--
ALTER TABLE `categorie_materiel`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `chat_session`
--
ALTER TABLE `chat_session`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `cinemas`
--
ALTER TABLE `cinemas`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `club_event`
--
ALTER TABLE `club_event`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `club_join_request`
--
ALTER TABLE `club_join_request`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `club_member`
--
ALTER TABLE `club_member`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `club_participation`
--
ALTER TABLE `club_participation`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `email_verification_token`
--
ALTER TABLE `email_verification_token`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `evenement`
--
ALTER TABLE `evenement`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `evenement_materiel`
--
ALTER TABLE `evenement_materiel`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `facture`
--
ALTER TABLE `facture`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `feedback`
--
ALTER TABLE `feedback`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `fidelity_history`
--
ALTER TABLE `fidelity_history`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `films`
--
ALTER TABLE `films`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `materiel`
--
ALTER TABLE `materiel`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `message`
--
ALTER TABLE `message`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `message_hidden`
--
ALTER TABLE `message_hidden`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `message_reaction`
--
ALTER TABLE `message_reaction`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `notification`
--
ALTER TABLE `notification`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `paiement`
--
ALTER TABLE `paiement`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `passkey_credentials`
--
ALTER TABLE `passkey_credentials`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `password_reset_token`
--
ALTER TABLE `password_reset_token`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `permission`
--
ALTER TABLE `permission`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT pour la table `qr_codes`
--
ALTER TABLE `qr_codes`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `reservation_cinema`
--
ALTER TABLE `reservation_cinema`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `reservation_evenement`
--
ALTER TABLE `reservation_evenement`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `reservation_materiel`
--
ALTER TABLE `reservation_materiel`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `role`
--
ALTER TABLE `role`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT pour la table `salles_cinema`
--
ALTER TABLE `salles_cinema`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `salle_evenement`
--
ALTER TABLE `salle_evenement`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `seance`
--
ALTER TABLE `seance`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `seats`
--
ALTER TABLE `seats`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `seat_rows`
--
ALTER TABLE `seat_rows`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `user`
--
ALTER TABLE `user`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT pour la table `user_abonnement`
--
ALTER TABLE `user_abonnement`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `carte_fidelite`
--
ALTER TABLE `carte_fidelite`
  ADD CONSTRAINT `FKgcw62a8slja15nlhokyccsmhl` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Contraintes pour la table `club_event`
--
ALTER TABLE `club_event`
  ADD CONSTRAINT `FKi2h9w0o5q2wxx1a6h1rgnc4bf` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`);

--
-- Contraintes pour la table `club_join_request`
--
ALTER TABLE `club_join_request`
  ADD CONSTRAINT `FKo61r30ilmgmlj4bjfsojvr709` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Contraintes pour la table `club_member`
--
ALTER TABLE `club_member`
  ADD CONSTRAINT `FKdsyvp62acbfu2dkc2ad23gsro` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `FKf6tl19ih8acrmheidn4xos2tx` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`);

--
-- Contraintes pour la table `club_participation`
--
ALTER TABLE `club_participation`
  ADD CONSTRAINT `FKj13cmgaherg89f3ox6xjgmrqf` FOREIGN KEY (`club_event_id`) REFERENCES `club_event` (`id`),
  ADD CONSTRAINT `FKqvq6sbt3fh3rde0rh99y9dl87` FOREIGN KEY (`club_member_id`) REFERENCES `club_member` (`id`);

--
-- Contraintes pour la table `email_verification_token`
--
ALTER TABLE `email_verification_token`
  ADD CONSTRAINT `FKqmvt3qcly3hbvde97srchdo3x` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Contraintes pour la table `evenement`
--
ALTER TABLE `evenement`
  ADD CONSTRAINT `FKx2glu4attb13wp73tbfjlc8y` FOREIGN KEY (`salle_id`) REFERENCES `salle_evenement` (`id`);

--
-- Contraintes pour la table `evenement_materiel`
--
ALTER TABLE `evenement_materiel`
  ADD CONSTRAINT `FKcjk5xy95tr8cdqk4cbtbvhwe` FOREIGN KEY (`materiel_id`) REFERENCES `materiel` (`id`),
  ADD CONSTRAINT `FKk1f0kak46fa8qu034c875fkdh` FOREIGN KEY (`evenement_id`) REFERENCES `evenement` (`id`);

--
-- Contraintes pour la table `feedback`
--
ALTER TABLE `feedback`
  ADD CONSTRAINT `FK7k33yw505d347mw3avr93akao` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `FKp3k1tyqn5injav3aba67tkyh0` FOREIGN KEY (`film_id`) REFERENCES `films` (`id`);

--
-- Contraintes pour la table `fidelity_history`
--
ALTER TABLE `fidelity_history`
  ADD CONSTRAINT `FK99cph0erd24o4q3mx4a49l275` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Contraintes pour la table `materiel`
--
ALTER TABLE `materiel`
  ADD CONSTRAINT `FK9rtmkvoq46ophu2yjv6ire6qu` FOREIGN KEY (`categorie_id`) REFERENCES `categorie_materiel` (`id`);

--
-- Contraintes pour la table `notification`
--
ALTER TABLE `notification`
  ADD CONSTRAINT `FKb0yvoep4h4k92ipon31wmdf7e` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Contraintes pour la table `paiement`
--
ALTER TABLE `paiement`
  ADD CONSTRAINT `FKcdf4b0vtaiu864ir0bhwm0jog` FOREIGN KEY (`facture_id`) REFERENCES `facture` (`id`);

--
-- Contraintes pour la table `passkey_credentials`
--
ALTER TABLE `passkey_credentials`
  ADD CONSTRAINT `FKjrgl4avsbi2hhu9qbuvyqyxkh` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Contraintes pour la table `password_reset_token`
--
ALTER TABLE `password_reset_token`
  ADD CONSTRAINT `FK5lwtbncug84d4ero33v3cfxvl` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Contraintes pour la table `qr_codes`
--
ALTER TABLE `qr_codes`
  ADD CONSTRAINT `FKbl0jbsts1arqojy89pctws1ug` FOREIGN KEY (`user_abonnement_id`) REFERENCES `user_abonnement` (`id`);

--
-- Contraintes pour la table `reservation_cinema`
--
ALTER TABLE `reservation_cinema`
  ADD CONSTRAINT `FK75s11wbv3ymu14lxmgfyu9q8g` FOREIGN KEY (`paiement_id`) REFERENCES `paiement` (`id`),
  ADD CONSTRAINT `FKckk4uo2rq7vj3d1foqna0h0dg` FOREIGN KEY (`seance_id`) REFERENCES `seance` (`id`),
  ADD CONSTRAINT `FKcn8dpd7uey7owbme0w6k4lg4a` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `FKjghpx2douqo96r6tdk010fr0h` FOREIGN KEY (`seat_id`) REFERENCES `seats` (`id`);

--
-- Contraintes pour la table `reservation_evenement`
--
ALTER TABLE `reservation_evenement`
  ADD CONSTRAINT `FK4n0uyk4899vapmw43meria9it` FOREIGN KEY (`paiement_id`) REFERENCES `paiement` (`id`),
  ADD CONSTRAINT `FKfm82fyf9hj2b3rd78xyca0jhp` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `FKp4amyyu06w9i8j2p90xm4lhgn` FOREIGN KEY (`evenement_id`) REFERENCES `evenement` (`id`);

--
-- Contraintes pour la table `reservation_materiel`
--
ALTER TABLE `reservation_materiel`
  ADD CONSTRAINT `FK320jpves31oq0ohoftvxpatni` FOREIGN KEY (`materiel_id`) REFERENCES `materiel` (`id`),
  ADD CONSTRAINT `FK8umxr2ubrrgj6o47gommj5m7c` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Contraintes pour la table `role_permissions`
--
ALTER TABLE `role_permissions`
  ADD CONSTRAINT `FKh0v7u4w7mttcu81o8wegayr8e` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`),
  ADD CONSTRAINT `FKlodb7xh4a2xjv39gc3lsop95n` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`);

--
-- Contraintes pour la table `salles_cinema`
--
ALTER TABLE `salles_cinema`
  ADD CONSTRAINT `FKqyey6jbo2xvp7ih34otbh60vh` FOREIGN KEY (`cinema_id`) REFERENCES `cinemas` (`id`);

--
-- Contraintes pour la table `seance`
--
ALTER TABLE `seance`
  ADD CONSTRAINT `FK4bp2pfqtmghepeangytt27lnn` FOREIGN KEY (`salle_cinema_id`) REFERENCES `salles_cinema` (`id`),
  ADD CONSTRAINT `FK4e8g1b9ybe9mjno91iw09o0jp` FOREIGN KEY (`film_id`) REFERENCES `films` (`id`);

--
-- Contraintes pour la table `seats`
--
ALTER TABLE `seats`
  ADD CONSTRAINT `FKdq696jb1s2wj0j7toyis3dqy2` FOREIGN KEY (`seance_id`) REFERENCES `seance` (`id`),
  ADD CONSTRAINT `FKo369nb56gnok892eb8c0mbgkw` FOREIGN KEY (`row_id`) REFERENCES `seat_rows` (`id`);

--
-- Contraintes pour la table `seat_rows`
--
ALTER TABLE `seat_rows`
  ADD CONSTRAINT `FK1gqb698gd2qfg24wu3kbcxl23` FOREIGN KEY (`salle_id`) REFERENCES `salles_cinema` (`id`);

--
-- Contraintes pour la table `user_abonnement`
--
ALTER TABLE `user_abonnement`
  ADD CONSTRAINT `FKjg3ak49t7qwpbux5mykhq9jq2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `FKjm9ges5fmbcaym63yf8fmingq` FOREIGN KEY (`abonnement_id`) REFERENCES `abonnement` (`id`);

--
-- Contraintes pour la table `user_roles`
--
ALTER TABLE `user_roles`
  ADD CONSTRAINT `FK55itppkw3i07do3h7qoclqd4k` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `FKrhfovtciq1l558cw6udg0h0d3` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
