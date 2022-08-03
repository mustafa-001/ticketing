package mutlu.ticketingapp.controller;


import mutlu.ticketingapp.dto.CreateTicketDto;
import mutlu.ticketingapp.dto.GetTicketDto;
import mutlu.ticketingapp.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
