package org.acme.taskmanager.resource;

import io.vertx.ext.web.RoutingContext;
import org.acme.taskmanager.dto.UserDTO;
import org.acme.taskmanager.service.CategoryService;
import org.acme.taskmanager.session.SessionUtils;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST API for session management.
 * Provides simple demo authentication with username only.
 */
@Path("/api/session")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Session", description = "Session management and authentication endpoints")
public class SessionResource {

    @Context
    private RoutingContext routingContext;

    @Inject
    private CategoryService categoryService;

    /**
     * Login endpoint - accepts username only (no password for demo).
     * Creates a new session and seeds default categories if needed.
     *
     * @param userDTO the user credentials (username only)
     * @return the logged-in user information
     */
    @POST
    @Path("/login")
    @Transactional
    @Operation(
        summary = "Login user",
        description = "Authenticates user with username only (demo mode). Creates session and seeds default categories on first login."
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = UserDTO.class)
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid request - username required"
        )
    })
    public Response login(@Valid final UserDTO userDTO) {
        // Store username in session
        SessionUtils.setCurrentUser(routingContext, userDTO.username());

        // Ensure default categories exist for this user
        categoryService.ensureDefaultCategories(userDTO.username());

        return Response.ok(userDTO).build();
    }

    /**
     * Get current user from session.
     *
     * @return the current user if authenticated, 401 otherwise
     */
    @GET
    @Path("/user")
    @Operation(
        summary = "Get current user",
        description = "Returns the currently authenticated user from session"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "User is authenticated",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = UserDTO.class)
            )
        ),
        @APIResponse(
            responseCode = "401",
            description = "User is not authenticated"
        )
    })
    public Response getCurrentUser() {
        try {
            String username = SessionUtils.getCurrentUserId(routingContext);
            return Response.ok(new UserDTO(username)).build();
        } catch (Exception e) {
            // Not authenticated
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    /**
     * Logout endpoint - invalidates current session.
     *
     * @return 204 No Content
     */
    @POST
    @Path("/logout")
    @Operation(
        summary = "Logout user",
        description = "Invalidates the current session and logs out the user"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "204",
            description = "Logout successful"
        )
    })
    public Response logout() {
        // Clear the user from session
        SessionUtils.clearCurrentUser(routingContext);

        // Destroy the entire session
        if (routingContext.session() != null) {
            routingContext.session().destroy();
        }

        return Response.noContent().build();
    }
}
