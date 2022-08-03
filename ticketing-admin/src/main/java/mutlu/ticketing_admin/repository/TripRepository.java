package mutlu.ticketing_admin.repository;

import mutlu.ticketing_admin.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {
}
