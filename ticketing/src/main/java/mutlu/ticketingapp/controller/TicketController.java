package mutlu.ticketingapp.controller;


import mutlu.ticketingapp.dto.*;
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
    public ticket.GetTicketDto add(@RequestBody @Valid ticket.CreateTicketDto request){
        return ticketService.addTicket(request);
    }

    @PostMapping("/buyBulk")
    public List<ticket.GetTicketDto> addAll(@RequestBody List<ticket.CreateTicketDto> requests){
        return ticketService.addTicketBulk(requests);
    }

    @GetMapping("/user/{id}")
    public List<ticket.GetTicketDto> getByUserId(@PathVariable Long userId){
        return  ticketService.getByUserId(userId);
    }

    @GetMapping("/search")
    public List<ticket.GetTripDto> search(@RequestBody ticket.SearchTripDto searchParameters){
        return ticketService.search(searchParameters);
    }
}
