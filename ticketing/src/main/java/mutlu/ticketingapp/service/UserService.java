package mutlu.ticketingapp.service;


import mutlu.ticketingapp.dto.*;
import mutlu.ticketingapp.entity.User;
import mutlu.ticketingapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AmqpTemplate rabbitTemplate;

    Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AmqpTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Creates and saves to database a new User entity with it's password hashed with {@link PasswordEncoder}.
     */
    public GetUserDto create(CreateUserDto request) {
        User user = new User();
        if (!request.firstPassword().equals(request.secondPassword())) {
            throw new InvalidParameterException("Passwords doesn't match.");
        }
        user.setUserType(request.userType())
                .setEmail(request.email())
                .setPhoneNumber(request.phoneNumber())
                .setFirstName(request.firstName())
                .setLastName(request.lastName())
                .setUserType(request.userType())
                .setPasswordHash(passwordEncoder.encode(request.firstPassword()));
        log.info("Saving new user: {}", user);
        //TODO Send email.
        return GetUserDto.fromUser(userRepository.save(user));
    }

    public Optional<GetUserDto> getByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        log.debug("User for id {} is {}", userId, user.orElse(null));
        //TODO
        return Optional.of(GetUserDto.fromUser(user.get()));
    }

    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    /**
     * Tries to log user in, if successful returns that User entity.
     */
    public GetUserDto login(LoginCredentialsDto credentialsDto) {
        return GetUserDto.fromUser(login(credentialsDto.email(), credentialsDto.password()));
    }

    /**
     * Tries to log user in, if successful returns that User entity.
     */
    private User login(String email, String passwordHash) {
        var userOpt = userRepository.findUserByEmail(email);
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
     * If logging user in is successfull (to make sure credentials right)
     * changes user's username.
     */
    public GetUserDto changeEmail(ChangeEmailDto request) {
        //Authenticate the user.
        var user = login(request.oldEmail(), request.password());
        if (!request.newEmailFirst().equals(request.newEmailSecond())){
            throw new IllegalArgumentException("Emails does not match.");
        }
        log.info("Changing email for {} to {}", user.getEmail(), request.newEmailFirst());
        user.setEmail(request.newEmailFirst());
        log.debug("Email before flush: {}", user.getEmail());
        userRepository.flush();
        log.debug("Email after flush: {}", user.getEmail());
        return  GetUserDto.fromUser(user);
    }

    /**
     * Compares {@link ChangePasswordDto}'s newPassword field's hash values and logs user.
     * If both is successful changes user's passwordHash fiels to newPassword's hash value.
     */
    public GetUserDto changePassword(ChangePasswordDto changePasswordDto) {
        //Authenticate the user.
        var user = login(changePasswordDto.email(), changePasswordDto.oldPassword());
        log.info("Changing password for {}: ", user.getEmail());
        if (changePasswordDto.newPasswordFirst().equals(changePasswordDto.newPasswordSecond())) {
            user.setPasswordHash(passwordEncoder.encode(changePasswordDto.newPasswordFirst()));
            log.debug("Password hash before flush {}", userRepository.findById(user.getUserId()).get().getPasswordHash());
            userRepository.flush();
            log.debug("Password hash after flush {}", userRepository.findById(user.getUserId()).get().getPasswordHash());
        } else {
            throw new RuntimeException("Passwords does not match.");
        }
        return GetUserDto.fromUser(user);
    }

}
