package mutlu.ticketing_admin.controller;

import mutlu.ticketing_admin.dto.*;
import mutlu.ticketing_admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public Optional<GetUserDto> getById(@PathVariable Long userId) {
        return userService.getByUserId(userId);
    }

    @PostMapping
    public GetUserDto add(@RequestBody @Valid CreateUserDto request) {
        return userService.create(request);
    }

    @PostMapping("/login")
    public GetUserDto login(@RequestBody LoginCredentialsDto loginCredentialsDto) {
        return userService.login(loginCredentialsDto);
    }

    @PostMapping("/changeEmail")
    public GetUserDto changeEmail(@RequestBody ChangeEmailDto changeEmailDto) {
        return userService.changeEmail(changeEmailDto);
    }

    @PostMapping("/changePassword")
    public GetUserDto changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        return userService.changePassword(changePasswordDto);
    }


    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
