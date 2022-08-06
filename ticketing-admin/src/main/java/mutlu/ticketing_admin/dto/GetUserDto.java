package mutlu.ticketing_admin.dto;

import mutlu.ticketing_admin.enums.UserType;
import mutlu.ticketing_admin.entity.User;

public record GetUserDto(Long userId, UserType userType, String email, String firstName, String lastName) {
    public static GetUserDto fromUser(User user) {
        return new GetUserDto(user.getUserId(), user.getUserType(), user.getEmail(), user.getFirstName(), user.getLastName());
    }
}
