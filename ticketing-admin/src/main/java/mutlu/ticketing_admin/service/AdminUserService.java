package mutlu.ticketing_admin.service;


import mutlu.ticketing_admin.dto.*;
import mutlu.ticketing_admin.entity.AdminUser;
import mutlu.ticketing_admin.exception.FieldsDoesNotMatchException;
import mutlu.ticketing_admin.exception.LoginException;
import mutlu.ticketing_admin.exception.UserAlreadyExistException;
import mutlu.ticketing_admin.repository.AdminUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${rabbitmq.email.queue}")
    private String emailQueueRoutingKey;

    @Autowired
    public AdminUserService(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder,
                            AmqpTemplate rabbitTemplate) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Creates and saves to database a new User entity with it's password hashed with {@link PasswordEncoder}.
     *
     * @throws IllegalArgumentException if passwords does not match.
     */
    public GetAdminUserDto create(CreateAdminUserDto request) {
        AdminUser adminUser = new AdminUser();
        if (adminUserRepository.findUserByEmail(request.email()).isPresent()){
            throw new UserAlreadyExistException();
        }
        if (!request.firstPassword().equals(request.secondPassword())) {
            throw new FieldsDoesNotMatchException("Password");
        }
        adminUser.setEmail(request.email())
                .setFirstName(request.firstName())
                .setLastName(request.lastName())
                .setPasswordHash(passwordEncoder.encode(request.firstPassword()));
        log.info("Saving new adminUser: {}", adminUser);
        adminUserRepository.save(adminUser);

        emailQueueRoutingKey = "ticketing.email";
        rabbitTemplate.convertAndSend(emailQueueRoutingKey, new RegistrationEmailDto(adminUser.getEmail(),
                adminUser.getFirstName(), adminUser.getLastName()));
        return GetAdminUserDto.fromAdminUser(adminUser);
    }

    /**
     * Return AdminUser with given userId.
     *
     * @throws IllegalArgumentException if it does not exist.
     */
    public Optional<GetAdminUserDto> getByUserId(Long userId) {
        Optional<AdminUser> user = adminUserRepository.findById(userId);
        log.debug("User for id {} is {}", userId, user.orElse(null));
        return Optional.of(GetAdminUserDto.fromAdminUser(user.orElseThrow(
                () -> new IllegalArgumentException("User with given" + " id cannot be found."))));
    }

    /**
     * Mark User with given userId as deleted to be excluded from future queries.
     *
     * @throws IllegalArgumentException if it does not exist.
     */
    public void delete(Long userId) {
        Optional<AdminUser> userOpt = adminUserRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User with given id cannot be found.");
        }
        log.info("Marking admin user {} as deleted.", userOpt.get());
        userOpt.get().setDeleted(true);
        adminUserRepository.flush();
    }


    /**
     * Tries to log user in, if successful returns that User entity.
     *
     * @throws LoginException .
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
                throw new LoginException();
            }
        } else {
            log.info("User doesn't exists: {}", email);
            throw new LoginException();
        }
    }

    /**
     * If logging user in is successful (to make sure credentials right)
     * changes User's username.
     *
     * @throws LoginException              If credentials wrong.
     * @throws FieldsDoesNotMatchException .
     */
    public GetAdminUserDto changeEmail(ChangeEmailDto request) {
        //Authenticate the user.
        var user = login(request.oldEmail(), request.password());
        if (!request.newEmailFirst().equals(request.newEmailSecond())) {
            throw new FieldsDoesNotMatchException("Email");
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
     *
     * @throws LoginException              .
     * @throws FieldsDoesNotMatchException .
     */
    public GetAdminUserDto changePassword(ChangePasswordDto changePasswordDto) {
        //Authenticate the user.
        var user = login(changePasswordDto.email(), changePasswordDto.oldPassword());
        log.info("Changing password for {}: ", user.getEmail());
        if (changePasswordDto.newPasswordFirst().equals(changePasswordDto.newPasswordSecond())) {
            user.setPasswordHash(passwordEncoder.encode(changePasswordDto.newPasswordFirst()));
            log.debug("Password hash before flush {}",
                    adminUserRepository.findById(user.getUserId()).orElseThrow().getPasswordHash());
            adminUserRepository.flush();
            log.debug("Password hash after flush {}",
                    adminUserRepository.findById(user.getUserId()).orElseThrow().getPasswordHash());
        } else {
            throw new FieldsDoesNotMatchException("Password");
        }
        return GetAdminUserDto.fromAdminUser(user);
    }

}
