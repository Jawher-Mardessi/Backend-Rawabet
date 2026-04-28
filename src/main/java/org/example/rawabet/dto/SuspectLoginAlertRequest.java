package org.example.rawabet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload envoyé par le frontend après 5 tentatives de connexion échouées.
 * Contient la photo capturée (base64) + métadonnées.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuspectLoginAlertRequest {

    /** Email utilisé lors des tentatives */
    private String email;

    /** Image capturée par la caméra — base64 data URI (ex: data:image/jpeg;base64,...) */
    private String photoBase64;

    /** Timestamp ISO côté client */
    private String timestamp;

    /** IP transmise par le frontend (optionnelle) */
    private String clientIp;
}