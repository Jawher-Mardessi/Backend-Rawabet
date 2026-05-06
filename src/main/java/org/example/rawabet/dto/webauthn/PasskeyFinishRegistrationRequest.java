package org.example.rawabet.dto.webauthn;

import com.fasterxml.jackson.databind.JsonNode;

public record PasskeyFinishRegistrationRequest(String requestId, JsonNode credential) {
}