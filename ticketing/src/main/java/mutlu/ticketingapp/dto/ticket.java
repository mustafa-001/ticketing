package mutlu.ticketingapp.dto;

import mutlu.ticketingapp.common.PassengerGender;
import mutlu.ticketingapp.common.PaymentType;
import mutlu.ticketingapp.common.VehicleType;
import mutlu.ticketingapp.entity.Ticket;
import mutlu.ticketingapp.entity.Trip;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ticket {
    public static record PaymentRequestDto(ClientPaymentInfoDto clientPaymentInfo, Long userId, Long tripId, BigDecimal totalAmount) {
    }

    public static record SearchTripDto(@NotBlank String departureStation,
                                       @NotBlank String arrivalStation,
                                       VehicleType vehicleType,
                                       @FutureOrPresent LocalDate date) {
    }

    public static record GetTripDto(Long tripId, VehicleType vehicleType, String departureStation, String arrivalStation,
                                    LocalDateTime departureTime, BigDecimal price) {

        public static GetTripDto fromTrip(Trip trip) {
            return new GetTripDto(trip.getTripId(), trip.getVehicleType(), trip.getDepartureStation(),
                    trip.getArrivalStation(), trip.getDeparture(), trip.getPrice());
        }
    }

    public static record GetTicketDto(Long ticketId, Long userId, Long tripId, PassengerGender passengerGender) {
        public static GetTicketDto fromTicket(Ticket ticket){
            return  new GetTicketDto(ticket.getTicketId(), ticket.getUser().getUserId(), ticket.getTrip().getTripId(), ticket.getPassengerGender());
        }
    }

    public static record CreateTicketDto(@NotNull Long userId, @NotNull Long tripId, @NotNull  PassengerGender passengerGender, @NotNull  ClientPaymentInfoDto clientPaymentInfoDto) {
    }

    public static record ClientPaymentInfoDto(PaymentType paymentType, String cardNumber) {
    }
}
