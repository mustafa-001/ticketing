package mutlu.ticketingapp.dto.ticket;

import mutlu.ticketingapp.common.PassengerGender;
import mutlu.ticketingapp.entity.Ticket;

public record GetTicketDto(Long ticketId, Long userId, Long tripId, PassengerGender passengerGender) {
    public static GetTicketDto fromTicket(Ticket ticket) {
        return new GetTicketDto(ticket.getTicketId(), ticket.getUser().getUserId(), ticket.getTrip().getTripId(), ticket.getPassengerGender());
    }
}
