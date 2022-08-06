package mutlu.ticketing_admin.exception;

public abstract class AbstractTicketingException extends RuntimeException {
    public AbstractTicketingException() {
    }
    public AbstractTicketingException(String message) {
        super(message);
    }

    public AbstractTicketingException(String message, Throwable cause) {
        super(message, cause);
    }

    public AbstractTicketingException(Throwable cause) {
        super(cause);
    }

    public AbstractTicketingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
