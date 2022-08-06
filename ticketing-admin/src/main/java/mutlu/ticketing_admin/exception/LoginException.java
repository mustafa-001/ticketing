package mutlu.ticketing_admin.exception;

public class LoginException extends AbstractTicketingException {
    public LoginException() {
        super("Wrong password or email.");
    }
}
