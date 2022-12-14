package mutlu.ticketingapp.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import mutlu.ticketingapp.enums.PassengerGender;

import javax.persistence.*;

/**
 * Entity representing a bought ticket.
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "ticketId")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ticketId;
    @ManyToOne
    @JoinColumn
    private User user;

    private PassengerGender passengerGender;

    @ManyToOne
    @JoinColumn
    private Trip trip;

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public User getUser() {
        return user;
    }

    public Ticket setUser(User user) {
        this.user = user;
        return this;
    }

    public Trip getTrip() {
        return trip;
    }

    public Ticket setTrip(Trip trip) {
        this.trip = trip;
        return this;
    }

    public PassengerGender getPassengerGender() {
        return passengerGender;
    }

    public Ticket setPassengerGender(PassengerGender passengerGender) {
        this.passengerGender = passengerGender;
        return this;
    }
}
