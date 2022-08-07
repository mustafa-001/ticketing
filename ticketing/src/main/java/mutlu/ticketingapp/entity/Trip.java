package mutlu.ticketingapp.entity;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import mutlu.ticketingapp.enums.VehicleType;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a Trip. This entity is managed by ticketing-admin service.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "tripId")
@Where(clause = "is_cancelled=false") //Do not return cancelled entitites to client.
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

    private Boolean isCancelled = false;
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

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getDepartureStation() {
        return departureStation;
    }

    public void setDepartureStation(String departureStation) {
        this.departureStation = departureStation;
    }

    public String getArrivalStation() {
        return arrivalStation;
    }

    public void setArrivalStation(String arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    public LocalDateTime getDeparture() {
        return departure;
    }

    public void setDeparture(LocalDateTime departure) {
        this.departure = departure;
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

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getCancelled() {
        return isCancelled;
    }

    public void setCancelled(Boolean cancelled) {
        isCancelled = cancelled;
    }
}
