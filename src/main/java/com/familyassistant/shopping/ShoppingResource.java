package com.familyassistant.shopping;

import com.familyassistant.ai.GeminiService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/shopping")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShoppingResource {

    @Inject
    GeminiService geminiService;

    @POST
    @Path("/sort")
    public SortResponse sort(SortRequest request) {
        var sorted = geminiService.sortForStore(request.items(), request.storeProfile());
        return new SortResponse(sorted);
    }
}
