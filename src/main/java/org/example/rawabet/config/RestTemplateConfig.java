package org.example.rawabet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);        // 5s pour établir la connexion
        factory.setReadTimeout(600000);         // 10 min pour la réponse (chargement modèle DeepFace)
        return new RestTemplate(factory);
    }
}