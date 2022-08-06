package mutlu.ticketing_admin.entity;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import mutlu.ticketing_admin.common.VehicleType;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//If another entity includes a Trip field when serializing/deserializing refer that field with it userId.
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "tripId")
@Entity
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long tripId;
    @NotNull
    private VehicleType vehicleType;
    @NotBlank
    private String departureStation;
    @NotBlank
    private String arrivalStation;
    @FutureOrPresent
    private LocalDateTime departure;

    @NotNull
    private BigDecimal price;
    @OneToMany(mappedBy = "trip")
    private List<Ticket> ticketList = new ArrayList<>();

    public Long getTripId() {
        return tripId;
    }

    public Trip setTripId(Long tripId) {
        this.tripId = tripId;
        return this;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public Trip setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
        return this;
    }

    public String getDepartureStation() {
        return departureStation;
    }

    public Trip setDepartureStation(String departureStation) {
        this.departureStation = departureStation;
        return this;
    }

    public String getArrivalStation() {
        return arrivalStation;
    }

    public Trip setArrivalStation(String arrivalStation) {
        this.arrivalStation = arrivalStation;
        return this;
    }

    public LocalDateTime getDeparture() {
        return departure;
    }

    public Trip setDeparture(LocalDateTime departure) {
        this.departure = departure;
        return this;
    }

    public List<Ticket> getTicketList() {
        return ticketList;
    }

    public Trip setTicketList(List<Ticket> ticketList) {
        this.ticketList = ticketList;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Trip setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }
}
