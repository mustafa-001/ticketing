package mutlu.ticketingapp.exception;

import mutlu.ticketingapp.common.PaymentResponse;

public class PaymentException extends AbstractTicketingException {
    public PaymentResponse getResponse() {
        return response;
    }
    private final PaymentResponse response;
    public PaymentException(PaymentResponse response) {
        super("An error occured when processing payment: "+ response);
        this.response = response;
    }
}
