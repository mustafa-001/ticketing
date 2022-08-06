package mutlu.ticketingapp.exception;

public class CannotSellTicketsException extends AbstractTicketingException {
    public CannotSellTicketsException(String reason) {
        super(reason);
    }
}
