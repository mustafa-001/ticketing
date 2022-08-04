package mutlu.ticketingapp.exception;

public class LoginExceptionAbstract extends AbstractTicketingException {
    public LoginExceptionAbstract() {
        super("Wrong password or email.");
    }
}
