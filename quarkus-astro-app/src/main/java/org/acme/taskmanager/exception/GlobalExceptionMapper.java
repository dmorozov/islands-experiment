package org.acme.taskmanager.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.acme.taskmanager.dto.ErrorDTO;
import org.jboss.logging.Logger;

/**
 * Global exception handler that maps application exceptions to HTTP responses.
 *
 * <p>This class implements JAX-RS {@link ExceptionMapper} to provide centralized exception
 * handling for the entire REST API. It converts custom exceptions into standardized JSON error
 * responses using {@link ErrorDTO}.
 *
 * <p><b>Exception Mappings:</b>
 *
 * <ul>
 *   <li>{@link ResourceNotFoundException} → HTTP 404 (Not Found)
 *   <li>{@link ValidationException} → HTTP 400 (Bad Request)
 *   <li>{@link UnauthorizedException} → HTTP 401 (Unauthorized)
 *   <li>{@link Exception} (generic) → HTTP 500 (Internal Server Error)
 * </ul>
 *
 * <p><b>Security Note:</b> Generic exceptions (500 errors) hide implementation details from
 * clients while logging full stack traces server-side for debugging.
 *
 * <p><b>Usage:</b> This class is automatically registered as a JAX-RS provider via the
 * {@code @Provider} annotation. No manual configuration is required.
 *
 * <pre>{@code
 * // In your resource class
 * @GET
 * @Path("/{id}")
 * public TaskResponseDTO getTask(@PathParam("id") UUID id) {
 *   Task task = taskRepository.findById(id);
 *   if (task == null) {
 *     throw new ResourceNotFoundException("Task not found");
 *     // Automatically converted to 404 with ErrorDTO
 *   }
 *   return TaskResponseDTO.from(task);
 * }
 * }</pre>
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

  private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

  /**
   * Converts an exception into an HTTP response with appropriate status code and error body.
   *
   * @param exception the exception to convert
   * @return HTTP response with status code and ErrorDTO body
   */
  @Override
  public Response toResponse(final Exception exception) {
    // Log all exceptions for debugging
    LOG.error("Exception occurred during request processing", exception);

    // Handle ResourceNotFoundException → 404
    if (exception instanceof ResourceNotFoundException) {
      return handleResourceNotFoundException((ResourceNotFoundException) exception);
    }

    // Handle ValidationException → 400
    if (exception instanceof ValidationException) {
      return handleValidationException((ValidationException) exception);
    }

    // Handle UnauthorizedException → 401
    if (exception instanceof UnauthorizedException) {
      return handleUnauthorizedException((UnauthorizedException) exception);
    }

    // Handle all other exceptions → 500
    return handleGenericException(exception);
  }

  /**
   * Handles ResourceNotFoundException by returning a 404 response.
   *
   * @param exception the ResourceNotFoundException
   * @return HTTP 404 response with ErrorDTO
   */
  private Response handleResourceNotFoundException(final ResourceNotFoundException exception) {
    LOG.debugf("Resource not found: %s", exception.getMessage());

    ErrorDTO errorDTO = ErrorDTO.of(exception.getMessage(), "NOT_FOUND");

    return Response.status(Response.Status.NOT_FOUND).entity(errorDTO).build();
  }

  /**
   * Handles ValidationException by returning a 400 response.
   *
   * @param exception the ValidationException
   * @return HTTP 400 response with ErrorDTO
   */
  private Response handleValidationException(final ValidationException exception) {
    LOG.debugf("Validation failed: %s", exception.getMessage());

    ErrorDTO errorDTO;
    if (exception.getField() != null) {
      // Field-specific validation error
      errorDTO = ErrorDTO.fieldError(exception.getMessage(), exception.getField(), "VALIDATION_ERROR");
    } else {
      // General validation error
      errorDTO = ErrorDTO.of(exception.getMessage(), "VALIDATION_ERROR");
    }

    return Response.status(Response.Status.BAD_REQUEST).entity(errorDTO).build();
  }

  /**
   * Handles UnauthorizedException by returning a 401 response.
   *
   * @param exception the UnauthorizedException
   * @return HTTP 401 response with ErrorDTO
   */
  private Response handleUnauthorizedException(final UnauthorizedException exception) {
    LOG.debugf("Unauthorized access attempt: %s", exception.getMessage());

    ErrorDTO errorDTO = ErrorDTO.of(exception.getMessage(), "UNAUTHORIZED");

    return Response.status(Response.Status.UNAUTHORIZED).entity(errorDTO).build();
  }

  /**
   * Handles generic exceptions by returning a 500 response.
   *
   * <p>This method intentionally returns a generic error message to clients to avoid leaking
   * implementation details. The full exception is logged server-side for debugging.
   *
   * @param exception the generic exception
   * @return HTTP 500 response with ErrorDTO
   */
  private Response handleGenericException(final Exception exception) {
    // Don't expose internal error details to clients
    LOG.error("Unexpected error occurred", exception);

    ErrorDTO errorDTO =
        ErrorDTO.of(
            "An unexpected error occurred. Please try again later.", "INTERNAL_SERVER_ERROR");

    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorDTO).build();
  }
}
