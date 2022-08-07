package mutlu.ticketing_admin.config;

import mutlu.ticketing_admin.entity.AdminUser;
import mutlu.ticketing_admin.entity.Trip;
import mutlu.ticketing_admin.enums.VehicleType;
import mutlu.ticketing_admin.repository.AdminUserRepository;
import mutlu.ticketing_admin.repository.TripRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Component
public class DataGenerator {

    private final AdminUserRepository adminUserRepository;
    private final TripRepository tripRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger log = LoggerFactory.getLogger(DataGenerator.class);

    @Autowired
    public DataGenerator(AdminUserRepository adminUserRepository, TripRepository tripRepository,
                         PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.tripRepository = tripRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener
    public void appReady(ApplicationReadyEvent event) {
        if (adminUserRepository.findAll().size() != 0) {
            log.debug("Database already contains data, skipping to add samples.");
            return;
        }
        log.info("Database is empty, filling with sample data.");
        AdminUser user1 = new AdminUser();
        user1.setEmail("admin1@email.com");
        user1.setPasswordHash(passwordEncoder.encode("123456"));
        user1.setFirstName("Kullanıcı Adı");
        user1.setLastName("Kullanıcı Soyadı");
        adminUserRepository.save(user1);

        Trip trip1 = new Trip();
        trip1.setDepartureStation("Aydın");
        trip1.setArrivalStation("İzmir");
        trip1.setVehicleType(VehicleType.BUS);
        trip1.setPrice(BigDecimal.valueOf(100));
        trip1.setDeparture(LocalDateTime.now().plusDays(7));
        trip1.setTripId(100L);
        tripRepository.save(trip1);

        Trip trip2 = new Trip();
        trip2.setDepartureStation("İzmir");
        trip2.setArrivalStation("Aydın");
        trip2.setVehicleType(VehicleType.BUS);
        trip2.setPrice(BigDecimal.valueOf(100));
        trip2.setDeparture(LocalDateTime.now().plusDays(14));
        trip2.setTripId(101L);
        tripRepository.save(trip2);

        Trip trip3 = new Trip();
        trip3.setDepartureStation("Aydın");
        trip3.setArrivalStation("İzmir");
        trip3.setVehicleType(VehicleType.BUS);
        trip3.setPrice(BigDecimal.valueOf(100000));
        trip3.setDeparture(LocalDateTime.now().plusDays(7));
        trip3.setTripId(102L);
        trip3.setCancelled(true);
        tripRepository.save(trip3);


    }
}
