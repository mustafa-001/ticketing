package mutlu.ticketingapp.exception;

public class NotEnoughTicketsLeftException extends AbstractTicketingException {
    public NotEnoughTicketsLeftException() {
        super("This trip does not have enough unsold tickets.");
    }
}
