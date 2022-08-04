package mutlu.ticketingapp.exception;

public class NotEnoughTicketsLeftExceptionAbstract extends AbstractTicketingException {
    public NotEnoughTicketsLeftExceptionAbstract() {
        super("This trip does not have enough unsold tickets.");
    }
}
