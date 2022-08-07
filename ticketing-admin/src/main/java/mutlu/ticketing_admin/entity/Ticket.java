package mutlu.ticketing_admin.entity;

import javax.persistence.*;

 /**
  * Entity representing a bought ticket. This entity is managed by main
  * in the database.
  */
 @Entity
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

    public Trip getTrip() {
        return trip;
    }

    public Ticket setTrip(Trip trip) {
        this.trip = trip;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Ticket setUser(User user) {
        this.user = user;
        return this;
    }
}
