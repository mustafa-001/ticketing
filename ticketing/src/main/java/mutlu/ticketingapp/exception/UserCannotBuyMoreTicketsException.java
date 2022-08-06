package mutlu.ticketingapp.exception;

import mutlu.ticketingapp.enums.UserType;

public class UserCannotBuyMoreTicketsException extends AbstractTicketingException {
    private UserType userType;
    public UserCannotBuyMoreTicketsException(UserType userType) {
        super("A " + (userType == null ? "" : userType.toString()) + " user cannot by more than 5 ticket.");
        this.userType = userType;
    }

    public UserType getUserType() {
        return userType;
    }

}
