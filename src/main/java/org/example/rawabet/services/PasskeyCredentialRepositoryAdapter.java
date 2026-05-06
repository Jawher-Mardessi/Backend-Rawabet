package org.example.rawabet.services;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.PublicKeyCredentialType;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.exception.Base64UrlException;
import lombok.RequiredArgsConstructor;
import org.example.rawabet.entities.PasskeyCredential;
import org.example.rawabet.entities.User;
import org.example.rawabet.repositories.PasskeyCredentialRepository;
import org.example.rawabet.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PasskeyCredentialRepositoryAdapter implements CredentialRepository {

    private final PasskeyCredentialRepository passkeyCredentialRepository;
    private final UserRepository userRepository;

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        return userRepository.findByEmailIgnoreCase(username)
                .map(this::buildCredentialsForUser)
                .orElseGet(Set::of);
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        return userRepository.findByEmailIgnoreCase(username)
                .map(user -> new ByteArray(String.valueOf(user.getId()).getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        Long userId = parseUserId(userHandle);
        if (userId == null) {
            return Optional.empty();
        }

        return userRepository.findById(userId).map(User::getEmail);
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        Long userId = parseUserId(userHandle);
        if (userId == null) {
            return Optional.empty();
        }

        return passkeyCredentialRepository
                .findByCredentialIdAndUserHandle(credentialId.getBase64Url(), String.valueOf(userId))
                .map(this::toRegisteredCredential);
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        return passkeyCredentialRepository.findByCredentialId(credentialId.getBase64Url())
                .stream()
                .map(this::toRegisteredCredential)
                .collect(Collectors.toSet());
    }

    private Set<PublicKeyCredentialDescriptor> buildCredentialsForUser(User user) {
        String userHandle = String.valueOf(user.getId());
        List<PasskeyCredential> credentials = passkeyCredentialRepository.findByUserHandle(userHandle);
        return credentials.stream()
                .map(PasskeyCredential::getCredentialId)
                .map(this::toDescriptor)
                .collect(Collectors.toSet());
    }

    private RegisteredCredential toRegisteredCredential(PasskeyCredential credential) {
        return RegisteredCredential.builder()
                .credentialId(fromBase64Url(credential.getCredentialId()))
                .userHandle(new ByteArray(credential.getUserHandle().getBytes(StandardCharsets.UTF_8)))
                .publicKeyCose(fromBase64Url(credential.getPublicKeyCose()))
                .signatureCount(credential.getSignatureCount())
                .build();
    }

    private PublicKeyCredentialDescriptor toDescriptor(String credentialId) {
        return PublicKeyCredentialDescriptor.builder()
                .id(fromBase64Url(credentialId))
                .type(PublicKeyCredentialType.PUBLIC_KEY)
                .build();
    }

    private ByteArray fromBase64Url(String value) {
        try {
            return ByteArray.fromBase64Url(value);
        } catch (Base64UrlException e) {
            throw new IllegalArgumentException("Invalid base64url value", e);
        }
    }

    private Long parseUserId(ByteArray userHandle) {
        try {
            String raw = new String(userHandle.getBytes(), StandardCharsets.UTF_8).trim();
            if (raw.isEmpty()) {
                return null;
            }
            return Long.parseLong(raw);
        } catch (Exception ignored) {
            return null;
        }
    }
}