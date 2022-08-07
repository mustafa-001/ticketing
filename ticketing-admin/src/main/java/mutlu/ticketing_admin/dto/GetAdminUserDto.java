package mutlu.ticketing_admin.dto;

import mutlu.ticketing_admin.entity.AdminUser;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record GetAdminUserDto(@NotNull Long userId,
                              @Email String email,
                              @NotBlank String firstName,
                              @NotBlank String lastName) {
    public static GetAdminUserDto fromAdminUser(AdminUser user) {
        return new GetAdminUserDto(user.getUserId(), user.getEmail(), user.getFirstName(), user.getLastName());
    }
}
