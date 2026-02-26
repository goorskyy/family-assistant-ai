package com.familyassistant.ai;

import com.familyassistant.shopping.ShoppingItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GeminiService {

    private static final Logger LOG = Logger.getLogger(GeminiService.class);

    @RestClient
    GeminiClient geminiClient;

    @Inject
    ObjectMapper objectMapper;

    @ConfigProperty(name = "gemini.api-key")
    String apiKey;

    public List<ShoppingItem> sortForStore(List<ShoppingItem> items, String storeProfile) {
        var prompt = buildPrompt(items, storeProfile);
        var response = geminiClient.generate(apiKey, GeminiClient.GeminiRequest.of(prompt));
        return parseSortedItems(response.text(), items);
    }

    private String buildPrompt(List<ShoppingItem> items, String storeProfile) {
        var sb = new StringBuilder();
        sb.append("You are a grocery shopping assistant. Sort the following shopping list in the most efficient order for navigating the store.\n\n");
        sb.append("Store layout: ").append(storeProfile).append("\n\n");
        sb.append("Shopping list (0-based index, name, category):\n");
        for (int i = 0; i < items.size(); i++) {
            var item = items.get(i);
            sb.append(i).append(". ").append(item.name()).append(" [").append(item.category()).append("]\n");
        }
        sb.append("\nReturn ONLY a JSON array of the 0-based indices in the optimal shopping order. Example: [2,0,3,1]");
        return sb.toString();
    }

    private List<ShoppingItem> parseSortedItems(String text, List<ShoppingItem> original) {
        try {
            // Strip markdown code fences if present
            var cleaned = text.strip().replaceAll("(?s)```.*?\\n", "").replace("```", "").strip();
            int[] indices = objectMapper.readValue(cleaned, int[].class);
            var sorted = new ArrayList<ShoppingItem>(indices.length);
            for (int idx : indices) {
                if (idx >= 0 && idx < original.size()) {
                    sorted.add(original.get(idx));
                }
            }
            // Append any items not included in the AI response (safety net)
            if (sorted.size() < original.size()) {
                for (int i = 0; i < original.size(); i++) {
                    int finalI = i;
                    if (sorted.stream().noneMatch(s -> s.equals(original.get(finalI)))) {
                        sorted.add(original.get(i));
                    }
                }
            }
            return sorted;
        } catch (Exception e) {
            LOG.warnf("Failed to parse Gemini response, returning original order: %s", e.getMessage());
            return original;
        }
    }
}
