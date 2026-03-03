package sn.symmetry.spareparts.exception;

/**
 * Exception thrown when a user is not authenticated or their authentication is invalid.
 * This is distinct from AccessDeniedException which is for authorization failures.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
