package mutlu.ticketingapp.repository;

import mutlu.ticketingapp.entity.Ticket;
import mutlu.ticketingapp.entity.Trip;
import mutlu.ticketingapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserAndTrip(User user, Trip trip);
    List<Ticket> findByTrip(Trip trip);
    List<Ticket> findByUser(User user);
}
