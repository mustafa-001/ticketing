package mutlu.ticketingapp.dto;

import javax.validation.constraints.Email;
import java.util.List;

public record TicketInformationMessageDto(GetUserDto userDto, List<GetTicketDto> ticketDtoList) {
}
