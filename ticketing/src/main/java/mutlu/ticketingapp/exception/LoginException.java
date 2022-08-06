package mutlu.ticketingapp.exception;

public class LoginException extends AbstractTicketingException {
    public LoginException() {
        super("Wrong password or email.");
    }
}
