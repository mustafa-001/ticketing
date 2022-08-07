package mutlu.ticketing_admin.controller;

import mutlu.ticketing_admin.dto.*;
import mutlu.ticketing_admin.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;


@RestController
@RequestMapping("/admins")
public class AdminUserController {
    private final AdminUserService adminUserService;

    @Autowired
    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/{userId}")
    public Optional<GetAdminUserDto> getById(@PathVariable Long userId) {
        return adminUserService.getByUserId(userId);
    }

    @PostMapping
    public GetAdminUserDto add(@RequestBody @Valid CreateAdminUserDto request) {
        return adminUserService.create(request);
    }

    @PostMapping("/login")
    public GetAdminUserDto login(@RequestBody LoginCredentialsDto loginCredentialsDto) {
        return adminUserService.login(loginCredentialsDto);
    }

    @PostMapping("/changeEmail")
    public GetAdminUserDto changeEmail(@RequestBody ChangeEmailDto changeEmailDto) {
        return adminUserService.changeEmail(changeEmailDto);
    }

    @PostMapping("/changePassword")
    public GetAdminUserDto changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        return adminUserService.changePassword(changePasswordDto);
    }


    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        adminUserService.delete(userId);
    }
}

