package com.familyassistant.core.ai;

/**
 * Core AI port. Domain modules depend on this interface, never on a specific AI provider.
 */
public interface AiPort {
    String complete(String prompt);
}
