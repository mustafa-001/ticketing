package mutlu.ticketing_admin.dto;

import mutlu.ticketing_admin.entity.AdminUser;

public record GetAdminUserDto(Long userId, String email, String firstName, String lastName) {
    public static GetAdminUserDto fromAdminUser(AdminUser user) {
        return new GetAdminUserDto(user.getUserId(), user.getEmail(), user.getFirstName(), user.getLastName());
    }
}
