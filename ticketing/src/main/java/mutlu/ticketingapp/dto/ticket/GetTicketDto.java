package mutlu.ticketingapp.dto.ticket;

import mutlu.ticketingapp.enums.PassengerGender;
import mutlu.ticketingapp.entity.Ticket;

import javax.validation.constraints.NotNull;

public record GetTicketDto(@NotNull Long ticketId, @NotNull Long userId, @NotNull Long tripId,
                           @NotNull PassengerGender passengerGender) {
    public static GetTicketDto fromTicket(Ticket ticket) {
        return new GetTicketDto(ticket.getTicketId(), ticket.getUser().getUserId(), ticket.getTrip().getTripId(), ticket.getPassengerGender());
    }
}
