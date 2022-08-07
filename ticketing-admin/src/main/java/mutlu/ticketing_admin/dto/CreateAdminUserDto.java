package mutlu.ticketing_admin.dto;

import javax.validation.constraints.NotBlank;

public record CreateAdminUserDto(@NotBlank String email,
                                 @NotBlank String firstName,
                                 @NotBlank String lastName,
                                 @NotBlank String firstPassword,
                                 @NotBlank String secondPassword) {
}
