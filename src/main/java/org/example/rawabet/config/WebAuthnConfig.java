package org.example.rawabet.config;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.PublicKeyCredentialParameters;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.List;
import java.util.Set;

@Configuration
public class WebAuthnConfig {

    @Bean
    public RelyingParty relyingParty(
            CredentialRepository credentialRepository,
            @Value("${app.frontend.url}") String frontendUrl
    ) {
        URI frontend = URI.create(frontendUrl);
        String rpId = frontend.getHost() != null ? frontend.getHost() : "localhost";
        String origin = frontend.getScheme() + "://" + frontend.getAuthority();

        return RelyingParty.builder()
                .identity(RelyingPartyIdentity.builder()
                        .id(rpId)
                        .name("Rawabet")
                        .build())
                .credentialRepository(credentialRepository)
                .origins(Set.of(origin))
                .preferredPubkeyParams(List.of(
                        PublicKeyCredentialParameters.ES256,
                        PublicKeyCredentialParameters.RS256
                ))
                .build();
    }
}