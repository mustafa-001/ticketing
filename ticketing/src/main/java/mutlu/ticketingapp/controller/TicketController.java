package mutlu.ticketingapp.controller;


import mutlu.ticketingapp.dto.ticket.CreateTicketDto;
import mutlu.ticketingapp.dto.ticket.GetTicketDto;
import mutlu.ticketingapp.dto.ticket.GetTripDto;
import mutlu.ticketingapp.dto.ticket.SearchTripDto;
import mutlu.ticketingapp.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    private final TicketService ticketService;

    @PostMapping("/buy")
    public GetTicketDto add(@RequestBody @Valid CreateTicketDto request){
        return ticketService.addTicket(request);
    }

    @PostMapping("/buyBulk")
    public List<GetTicketDto> addAll(@RequestBody @Valid List<CreateTicketDto> requests){
        return ticketService.addTicketBulk(requests);
    }

    @GetMapping("/user/{userId}")
    public List<GetTicketDto> getByUserId(@PathVariable Long userId){
        return ticketService.getByUserId(userId);
    }

    @GetMapping("/search")
    public List<GetTripDto> search(@RequestBody @Valid SearchTripDto searchParameters){
        return ticketService.search(searchParameters);
    }
}
