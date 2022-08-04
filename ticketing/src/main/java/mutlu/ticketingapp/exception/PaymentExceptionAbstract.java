package mutlu.ticketingapp.exception;

import mutlu.ticketingapp.common.PaymentResponse;

public class PaymentExceptionAbstract extends AbstractTicketingException {
    public PaymentResponse getResponse() {
        return response;
    }
    private final PaymentResponse response;
    public PaymentExceptionAbstract(PaymentResponse response) {
        super("An error occured when processing payment: "+ response.toString());
        this.response = response;
    }
}
