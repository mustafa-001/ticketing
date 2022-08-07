package mutlu.ticketingapp.config;

import mutlu.ticketingapp.entity.Ticket;
import mutlu.ticketingapp.entity.Trip;
import mutlu.ticketingapp.entity.User;
import mutlu.ticketingapp.enums.PassengerGender;
import mutlu.ticketingapp.enums.UserType;
import mutlu.ticketingapp.enums.VehicleType;
import mutlu.ticketingapp.repository.TicketRepository;
import mutlu.ticketingapp.repository.TripRepository;
import mutlu.ticketingapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.lang.Thread.sleep;


@Component
public class DataGenerator {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final PasswordEncoder passwordEncoder;
    private final TripRepository tripRepository;
    private final Logger log = LoggerFactory.getLogger(DataGenerator.class);

    @Autowired
    public DataGenerator(UserRepository userRepository, TicketRepository ticketRepository,
                         PasswordEncoder passwordEncoder, TripRepository tripRepository) {
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.passwordEncoder = passwordEncoder;
        this.tripRepository = tripRepository;
    }

    @EventListener
    public void appReady(ApplicationReadyEvent event) {
        if (userRepository.findAll().size() != 0 || ticketRepository.findAll().size() != 0) {
            log.debug("Database already contains data, skipping to add samples.");
            return;
        }
        log.info("Database is empty, filling with sample data.");

        //Normally populating trip database ticketing-admin service's responsibility but
        //to make sure we dp it here too anyway if it's empty. Another alternative is to sleep
        //for a second or two.
        if (tripRepository.findAll().isEmpty()){
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

        User user1 = new User();
        user1.setUserType(UserType.INDIVIDUAL);
        user1.setPhoneNumber("05660129876");
        user1.setEmail("user1@email.com");
        user1.setPasswordHash(passwordEncoder.encode("123456"));
        user1.setFirstName("Kullanıcı İsmi 1");
        user1.setLastName("Kullanıcı Soyadı 1");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUserType(UserType.CORPORATE);
        user2.setPhoneNumber("06661117777");
        user2.setEmail("user2@email.com");
        user2.setPasswordHash(passwordEncoder.encode("123456"));
        user2.setFirstName("Kullanıcı İsmi 2");
        user2.setLastName("Kullanıcı Soyadı 2 ");
        userRepository.save(user2);

        Ticket ticket1 = new Ticket();
        ticket1.setTrip(tripRepository.findAll().get(0));
        ticket1.setUser(user1);
        ticket1.setPassengerGender(PassengerGender.MALE);
        ticketRepository.save(ticket1);

        Ticket ticket2 = new Ticket();
        ticket2.setTrip(tripRepository.findAll().get(0));
        ticket2.setUser(user2);
        ticket2.setPassengerGender(PassengerGender.FEMALE);
        ticketRepository.save(ticket2);

        Ticket ticket3 = new Ticket();
        ticket3.setTrip(tripRepository.findAll().get(0));
        ticket3.setUser(user2);
        ticket3.setPassengerGender(PassengerGender.MALE);
        ticketRepository.save(ticket3);
    }
}
