package mutlu.ticketingapp.repository;

import mutlu.ticketingapp.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {
}
