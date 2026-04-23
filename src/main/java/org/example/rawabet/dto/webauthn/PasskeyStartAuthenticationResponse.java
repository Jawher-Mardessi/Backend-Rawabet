package org.example.rawabet.dto.webauthn;

import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;

public record PasskeyStartAuthenticationResponse(String requestId,
                                                 PublicKeyCredentialRequestOptions publicKeyCredentialRequestOptions) {
}