package mutlu.ticketingapp.exception;

import mutlu.ticketingapp.common.UserType;

public class UserCannotBuyMoreTicketsExceptionAbstract extends AbstractTicketingException {
    private UserType userType;
    public UserCannotBuyMoreTicketsExceptionAbstract(UserType userType) {
        super("A "+ userType.toString()+ " user cannot by more than 5 ticket.");
        this.userType = userType;
    }

    public UserType getUserType() {
        return userType;
    }

}
