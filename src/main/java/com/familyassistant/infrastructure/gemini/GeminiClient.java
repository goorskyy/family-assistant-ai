package com.familyassistant.infrastructure.gemini;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "gemini")
@Path("/v1beta/models/gemini-2.0-flash:generateContent")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
interface GeminiClient {

    @POST
    GeminiResponse generate(@QueryParam("key") String apiKey, GeminiRequest request);

    record GeminiRequest(List<Content> contents) {
        record Content(List<Part> parts) {}
        record Part(String text) {}

        static GeminiRequest of(String prompt) {
            return new GeminiRequest(List.of(new Content(List.of(new Part(prompt)))));
        }
    }

    record GeminiResponse(List<Candidate> candidates) {
        record Candidate(Content content) {}
        record Content(List<Part> parts) {}
        record Part(String text) {}

        String text() {
            return candidates().getFirst().content().parts().getFirst().text();
        }
    }
}
