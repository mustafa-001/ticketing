package com.mutlu.ticketingemailandsms.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GetTripDto(Long tripId, VehicleType vehicleType, String departureStation, String arrivalStation,
                         LocalDateTime departureTime, BigDecimal price) {
}
