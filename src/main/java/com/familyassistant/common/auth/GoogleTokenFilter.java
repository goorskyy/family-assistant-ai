package com.familyassistant.common.auth;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Provider
public class GoogleTokenFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(GoogleTokenFilter.class);
    private static final String TOKENINFO_URL = "https://oauth2.googleapis.com/tokeninfo?access_token=";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public void filter(ContainerRequestContext ctx) {
        var path = ctx.getUriInfo().getPath();
        if (path.startsWith("/q/")) {
            return; // Skip Quarkus dev endpoints (health, OpenAPI, Swagger)
        }

        var authHeader = ctx.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        var token = authHeader.substring(7);
        if (!isValidGoogleToken(token)) {
            ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private boolean isValidGoogleToken(String token) {
        try {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(TOKENINFO_URL + token))
                    .GET()
                    .build();
            var response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 200;
        } catch (Exception e) {
            LOG.warnf("Token verification failed: %s", e.getMessage());
            return false;
        }
    }
}
