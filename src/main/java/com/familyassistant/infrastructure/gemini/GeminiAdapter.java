package com.familyassistant.infrastructure.gemini;

import com.familyassistant.core.ai.AiPort;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class GeminiAdapter implements AiPort {

    @RestClient
    GeminiClient geminiClient;

    @ConfigProperty(name = "gemini.api-key")
    String apiKey;

    @Override
    public String complete(String prompt) {
        return geminiClient.generate(apiKey, GeminiClient.GeminiRequest.of(prompt)).text();
    }
}
