package com.familyassistant.infrastructure.gemini;

import com.familyassistant.core.ai.AiPort;
import com.google.genai.Client;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class GeminiAdapter implements AiPort {

    @ConfigProperty(name = "gemini.api.key")
    String apiKey;

    @ConfigProperty(name = "gemini.model", defaultValue = "gemini-2.0-flash")
    String model;

    private Client client;

    @PostConstruct
    void init() {
        client = Client.builder().apiKey(apiKey).build();
    }

    @Override
    public String complete(String prompt) {
        try {
            return client.models.generateContent(model, prompt, null).text();
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("429")) {
                throw new WebApplicationException("AI provider rate limit exceeded, try again later",
                        Response.Status.TOO_MANY_REQUESTS);
            }
            throw new WebApplicationException("AI provider error: " + msg, Response.Status.BAD_GATEWAY);
        }
    }
}
