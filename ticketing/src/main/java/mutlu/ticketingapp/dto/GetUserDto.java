package mutlu.ticketingapp.dto;

import mutlu.ticketingapp.common.UserType;
import mutlu.ticketingapp.entity.User;

public record GetUserDto(Long userId, UserType userType, String email, String firstName, String lastName) {
    public static GetUserDto fromUser(User user) {
        return new GetUserDto(user.getUserId(), user.getUserType(), user.getEmail(), user.getFirstName(), user.getLastName());
    }
}
