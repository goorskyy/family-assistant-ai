package com.familyassistant.shopping.api;

import com.familyassistant.shopping.application.ShoppingSortUseCase;
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
    ShoppingSortUseCase sortUseCase;

    @POST
    @Path("/sort")
    public SortResponse sort(SortRequest request) {
        return new SortResponse(sortUseCase.sort(request.items(), request.storeProfile()));
    }
}
