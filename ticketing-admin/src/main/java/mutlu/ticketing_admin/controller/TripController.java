package mutlu.ticketing_admin.controller;

import mutlu.ticketing_admin.dto.CreateTripDto;
import mutlu.ticketing_admin.dto.GetTripDto;
import mutlu.ticketing_admin.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Access;
import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/trips")
public class TripController {
    private final TripService tripService;

    @Autowired
    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    GetTripDto add(@RequestBody CreateTripDto request){
        return  tripService.addTrip(request);
    }

    @GetMapping("/{tripId}")
    Optional<GetTripDto> getTrip(@PathVariable Long tripId){
        return  tripService.getById(tripId);
    }

    @GetMapping("/getSoldTickets/{tripId}")
    Long getSoldTickets(@PathVariable Long tripId){
        return tripService.totalSoldTicketsFromTrip(tripId);
    }
    @GetMapping("/getRevenueFromTrip/{tripId}")
    BigDecimal getRevenueFromTrip(@PathVariable Long tripId){
        return tripService.totalRevenueFromTrip(tripId);
    }

    @DeleteMapping("/{tripId}")
    void cancel(@PathVariable Long tripId){
        tripService.cancel(tripId);
    }

}
