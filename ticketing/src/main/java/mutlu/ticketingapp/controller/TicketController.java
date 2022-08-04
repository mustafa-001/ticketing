package mutlu.ticketingapp.controller;


import mutlu.ticketingapp.dto.CreateTicketDto;
import mutlu.ticketingapp.dto.GetTicketDto;
import mutlu.ticketingapp.dto.GetTripDto;
import mutlu.ticketingapp.dto.SearchTripDto;
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
    public List<GetTicketDto> addAll(@RequestBody List<CreateTicketDto> requests){
        return ticketService.addTicketBulk(requests);
    }

    @GetMapping("/user/{id}")
    public List<GetTicketDto> getByUserId(@PathVariable Long userId){
        return  ticketService.getByUserId(userId);
    }

    @GetMapping("/search")
    public List<GetTripDto> search(@RequestBody SearchTripDto searchParameters){
        return ticketService.search(searchParameters);
    }
}
