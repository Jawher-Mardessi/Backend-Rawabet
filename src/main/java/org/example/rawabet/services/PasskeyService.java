package org.example.rawabet.services;

import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.FinishAssertionOptions;
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartAssertionOptions;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.UserIdentity;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.dto.AuthResponse;
import org.example.rawabet.dto.webauthn.PasskeyFinishAuthenticationRequest;
import org.example.rawabet.dto.webauthn.PasskeyFinishRegistrationRequest;
import org.example.rawabet.dto.webauthn.PasskeyStartAuthenticationRequest;
import org.example.rawabet.dto.webauthn.PasskeyStartAuthenticationResponse;
import org.example.rawabet.dto.webauthn.PasskeyStartRegistrationRequest;
import org.example.rawabet.dto.webauthn.PasskeyStartRegistrationResponse;
import org.example.rawabet.entities.PasskeyCredential;
import org.example.rawabet.entities.User;
import org.example.rawabet.entities.WebAuthnCeremony;
import org.example.rawabet.repositories.PasskeyCredentialRepository;
import org.example.rawabet.repositories.UserRepository;
import org.example.rawabet.repositories.WebAuthnCeremonyRepository;
import org.example.rawabet.security.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasskeyService {

    private static final int CEREMONY_TTL_MINUTES = 10;

    private final RelyingParty relyingParty;
    private final IAuthService authService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasskeyCredentialRepository passkeyCredentialRepository;
    private final WebAuthnCeremonyRepository ceremonyRepository;

    @Transactional
    public PasskeyStartRegistrationResponse startRegistration(PasskeyStartRegistrationRequest request) {
        User user = resolveRegistrationUser(request);
        ByteArray userHandle = encodeUserHandle(user.getId());

        String displayName = hasText(request.displayName()) ? request.displayName().trim() : user.getNom();
        if (!hasText(displayName)) {
            displayName = user.getEmail();
        }

        UserIdentity userIdentity = UserIdentity.builder()
                .name(user.getEmail())
                .displayName(displayName)
            .id(userHandle)
                .build();

        var options = relyingParty.startRegistration(
                StartRegistrationOptions.builder()
                        .user(userIdentity)
                        .timeout(60000L)
                        .build()
        );

        String requestId = UUID.randomUUID().toString();
        ceremonyRepository.save(new WebAuthnCeremony(
                requestId,
                WebAuthnCeremony.Purpose.REGISTRATION,
                toJson(options),
                user.getEmail(),
                userHandle.getBase64Url(),
                LocalDateTime.now().plusMinutes(CEREMONY_TTL_MINUTES),
                LocalDateTime.now(),
                false
        ));

        return new PasskeyStartRegistrationResponse(requestId, options);
    }

    @Transactional
    public void finishRegistration(PasskeyFinishRegistrationRequest request) {
        WebAuthnCeremony ceremony = loadCeremony(request.requestId(), WebAuthnCeremony.Purpose.REGISTRATION);

        try {
            var startOptions = com.yubico.webauthn.data.PublicKeyCredentialCreationOptions.fromJson(ceremony.getRequestJson());
            var response = PublicKeyCredential.parseRegistrationResponseJson(request.credential().toString());
            var result = relyingParty.finishRegistration(
                    FinishRegistrationOptions.builder()
                            .request(startOptions)
                            .response(response)
                            .build()
            );

            User user = resolveUserByEmailOrThrow(ceremony.getUsername());
            saveCredential(user, result.getKeyId().getId(), result.getPublicKeyCose(), result.getSignatureCount(), response);
            ceremony.setConsumed(true);
            ceremonyRepository.save(ceremony);
        } catch (Exception e) {
            throw new RuntimeException("Impossible de finaliser l'enregistrement passkey", e);
        }
    }

    @Transactional
    public PasskeyStartAuthenticationResponse startAuthentication(PasskeyStartAuthenticationRequest request) {
        StartAssertionOptions.StartAssertionOptionsBuilder builder = StartAssertionOptions.builder()
                .timeout(60000L);
        if (request != null && hasText(request.username())) {
            builder.username(request.username().trim());
        }

        AssertionRequest assertionRequest = relyingParty.startAssertion(builder.build());

        String requestId = UUID.randomUUID().toString();
        ceremonyRepository.save(new WebAuthnCeremony(
                requestId,
                WebAuthnCeremony.Purpose.AUTHENTICATION,
                toJson(assertionRequest),
                request != null && hasText(request.username()) ? request.username().trim() : null,
                null,
                LocalDateTime.now().plusMinutes(CEREMONY_TTL_MINUTES),
                LocalDateTime.now(),
                false
        ));

        return new PasskeyStartAuthenticationResponse(requestId, assertionRequest.getPublicKeyCredentialRequestOptions());
    }

    @Transactional
    public AuthResponse finishAuthentication(PasskeyFinishAuthenticationRequest request) {
        WebAuthnCeremony ceremony = loadCeremony(request.requestId(), WebAuthnCeremony.Purpose.AUTHENTICATION);

        try {
            AssertionRequest startOptions = AssertionRequest.fromJson(ceremony.getRequestJson());
            var response = PublicKeyCredential.parseAssertionResponseJson(request.credential().toString());
            var result = relyingParty.finishAssertion(
                    FinishAssertionOptions.builder()
                            .request(startOptions)
                            .response(response)
                            .build()
            );

            ByteArray userHandle = result.getCredential().getUserHandle();
            User user = resolveUserByHandle(userHandle);
            if (user == null) {
                throw new RuntimeException("Utilisateur introuvable pour cette passkey");
            }

            updateCredentialSignatureCount(result.getCredential().getCredentialId().getBase64Url(),
                    userHandle.getBase64Url(),
                    result.getCredential().getSignatureCount());

            ceremony.setConsumed(true);
            ceremonyRepository.save(ceremony);

            return new AuthResponse(jwtService.generateToken(user));
        } catch (Exception e) {
            throw new RuntimeException("Impossible de finaliser l'authentification passkey", e);
        }
    }

    private void saveCredential(User user, ByteArray credentialId, ByteArray publicKeyCose, long signatureCount,
                                PublicKeyCredential<?, ?> response) {
        String userHandle = String.valueOf(user.getId());
        PasskeyCredential credential = passkeyCredentialRepository
                .findByCredentialIdAndUserHandle(credentialId.getBase64Url(), userHandle)
                .orElseGet(PasskeyCredential::new);

        credential.setCredentialId(credentialId.getBase64Url());
        credential.setUserHandle(userHandle);
        credential.setUser(user);
        credential.setPublicKeyCose(publicKeyCose.getBase64Url());
        credential.setSignatureCount(signatureCount);
        credential.setBackupEligible(false);
        credential.setBackedUp(false);
        credential.setTransports(extractTransports(response));
        passkeyCredentialRepository.save(credential);
    }

    private void updateCredentialSignatureCount(String credentialId, String userHandle, long signatureCount) {
        passkeyCredentialRepository.findByCredentialIdAndUserHandle(credentialId, userHandle)
                .ifPresent(credential -> {
                    credential.setSignatureCount(signatureCount);
                    passkeyCredentialRepository.save(credential);
                });
    }

    private User resolveRegistrationUser(PasskeyStartRegistrationRequest request) {
        try {
            User authenticatedUser = authService.getAuthenticatedUser();
            if (authenticatedUser != null) {
                return authenticatedUser;
            }
        } catch (RuntimeException ignored) {
            // Anonymous registration fallback handled below.
        }

        if (request == null || !hasText(request.username())) {
            throw new RuntimeException("Nom d'utilisateur requis pour enregistrer une passkey");
        }

        return resolveUserByEmailOrThrow(request.username().trim());
    }

    private User resolveUserByEmailOrThrow(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    private User resolveUserByHandle(ByteArray userHandle) {
        Long id = parseUserHandle(userHandle);
        if (id == null) {
            return null;
        }
        return userRepository.findById(id).orElse(null);
    }

    private WebAuthnCeremony loadCeremony(String requestId, WebAuthnCeremony.Purpose purpose) {
        WebAuthnCeremony ceremony = ceremonyRepository
                .findByRequestIdAndPurposeAndConsumedFalse(requestId, purpose)
                .orElseThrow(() -> new RuntimeException("Cérémonie WebAuthn introuvable ou déjà utilisée"));

        if (ceremony.isExpired()) {
            ceremony.setConsumed(true);
            ceremonyRepository.save(ceremony);
            throw new RuntimeException("Cérémonie WebAuthn expirée");
        }

        return ceremony;
    }

    private String toJson(Object value) {
        try {
            if (value instanceof com.yubico.webauthn.AssertionRequest assertionRequest) {
                return assertionRequest.toJson();
            }
            if (value instanceof com.yubico.webauthn.data.PublicKeyCredentialCreationOptions creationOptions) {
                return creationOptions.toJson();
            }
            throw new IllegalArgumentException("Unsupported WebAuthn payload type: " + value.getClass().getName());
        } catch (Exception e) {
            throw new RuntimeException("Impossible de sérialiser la cérémonie WebAuthn", e);
        }
    }

    private String extractTransports(PublicKeyCredential<?, ?> response) {
        try {
            Object credentialResponse = response.getResponse();
            var method = credentialResponse.getClass().getMethod("getTransports");
            Object value = method.invoke(credentialResponse);
            if (value instanceof java.util.Optional<?> optional && optional.isPresent()) {
                Object raw = optional.get();
                if (raw instanceof java.util.Collection<?> collection && !collection.isEmpty()) {
                    return collection.stream().map(String::valueOf).reduce((left, right) -> left + "," + right).orElse(null);
                }
            }
        } catch (Exception ignored) {
            // Transport hints are optional.
        }
        return null;
    }

    private ByteArray encodeUserHandle(Long userId) {
        return new ByteArray(String.valueOf(userId).getBytes(StandardCharsets.UTF_8));
    }

    private Long parseUserHandle(ByteArray userHandle) {
        try {
            return Long.parseLong(new String(userHandle.getBytes(), StandardCharsets.UTF_8).trim());
        } catch (Exception e) {
            return null;
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}