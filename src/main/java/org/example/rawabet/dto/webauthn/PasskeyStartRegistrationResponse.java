package org.example.rawabet.dto.webauthn;

import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;

public record PasskeyStartRegistrationResponse(String requestId,
                                               PublicKeyCredentialCreationOptions publicKeyCredentialCreationOptions) {
}