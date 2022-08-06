package mutlu.ticketing_admin.service;


import mutlu.ticketing_admin.dto.*;
import mutlu.ticketing_admin.entity.AdminUser;
import mutlu.ticketing_admin.repository.AdminUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.Optional;

@Service
public class AdminUserService {
    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AmqpTemplate rabbitTemplate;

    Logger log = LoggerFactory.getLogger(AdminUserService.class);

    @Autowired
    public AdminUserService(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder, AmqpTemplate rabbitTemplate) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Creates and saves to database a new User entity with it's password hashed with {@link PasswordEncoder}.
     */
    public GetAdminUserDto create(CreateAdminUserDto request) {
        AdminUser user = new AdminUser();
        if (!request.firstPassword().equals(request.secondPassword())) {
            throw new InvalidParameterException("Passwords doesn't match.");
        }
        user.setEmail(request.email())
                .setFirstName(request.firstName())
                .setLastName(request.lastName())
                .setPasswordHash(passwordEncoder.encode(request.firstPassword()));
        log.info("Saving new user: {}", user);
        return GetAdminUserDto.fromAdminUser(adminUserRepository.save(user));
    }

    public Optional<GetAdminUserDto> getByUserId(Long userId) {
        Optional<AdminUser> user = adminUserRepository.findById(userId);
        log.debug("User for id {} is {}", userId, user.orElse(null));
        //TODO
        return Optional.of(GetAdminUserDto.fromAdminUser(user.get()));
    }

    public void delete(Long userId) {
        adminUserRepository.deleteById(userId);
    }

    /**
     * Tries to log user in, if successful returns that User entity.
     */
    public GetAdminUserDto login(LoginCredentialsDto credentialsDto) {
        return GetAdminUserDto.fromAdminUser(login(credentialsDto.email(), credentialsDto.password()));
    }

    /**
     * Tries to log user in, if successful returns that User entity.
     */
    private AdminUser login(String email, String passwordHash) {
        var userOpt = adminUserRepository.findUserByEmail(email);
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            if (passwordEncoder.matches(passwordHash, user.getPasswordHash())) {
                log.info("User logged in {}", user);
                return user;
            } else {
                log.info("Wrong password when logging in. Username: {}", email);
                //TODO Make custom login exception.
                throw new RuntimeException("Email or password is wrong.");
            }
        } else {
            log.info("User doesn't exists: {}", email);
            throw new RuntimeException("Email or password is wrong.");
        }
    }

    /**
     * If logging user in is successful (to make sure credentials right)
     * changes user's username.
     */
    public GetAdminUserDto changeEmail(ChangeEmailDto request) {
        //Authenticate the user.
        var user = login(request.oldEmail(), request.password());
        if (!request.newEmailFirst().equals(request.newEmailSecond())) {
            throw new IllegalArgumentException("Emails does not match.");
        }
        log.info("Changing email for {} to {}", user.getEmail(), request.newEmailFirst());
        user.setEmail(request.newEmailFirst());
        log.debug("Email before flush: {}", user.getEmail());
        adminUserRepository.flush();
        log.debug("Email after flush: {}", user.getEmail());
        return GetAdminUserDto.fromAdminUser(user);
    }

    /**
     * Compares {@link ChangePasswordDto}'s newPassword field's hash values and logs user.
     * If both is successful changes user's passwordHash fiels to newPassword's hash value.
     */
    public GetAdminUserDto changePassword(ChangePasswordDto changePasswordDto) {
        //Authenticate the user.
        var user = login(changePasswordDto.email(), changePasswordDto.oldPassword());
        log.info("Changing password for {}: ", user.getEmail());
        if (changePasswordDto.newPasswordFirst().equals(changePasswordDto.newPasswordSecond())) {
            user.setPasswordHash(passwordEncoder.encode(changePasswordDto.newPasswordFirst()));
            log.debug("Password hash before flush {}",
                    adminUserRepository.findById(user.getUserId()).get().getPasswordHash());
            adminUserRepository.flush();
            log.debug("Password hash after flush {}",
                    adminUserRepository.findById(user.getUserId()).get().getPasswordHash());
        } else {
            throw new RuntimeException("Passwords does not match.");
        }
        return GetAdminUserDto.fromAdminUser(user);
    }

}
