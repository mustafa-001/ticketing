package mutlu.ticketing_admin.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;

//If another entity includes a User field when serializing/deserializing refer that field with it userId.
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "ticketId")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ticketId;
    @ManyToOne
    @JoinColumn
    private User user;

    @ManyToOne
    @JoinColumn
    private Trip trip;

    public Long getTicketId() {
        return ticketId;
    }

    public Ticket setTicketId(Long ticketId) {
        this.ticketId = ticketId;
        return this;
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
}