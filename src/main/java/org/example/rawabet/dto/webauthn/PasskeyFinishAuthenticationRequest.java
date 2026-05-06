package org.example.rawabet.dto.webauthn;

import com.fasterxml.jackson.databind.JsonNode;

public record PasskeyFinishAuthenticationRequest(String requestId, JsonNode credential) {
}