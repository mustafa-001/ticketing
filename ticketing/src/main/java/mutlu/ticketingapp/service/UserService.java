package mutlu.ticketingapp.service;


import mutlu.ticketingapp.dto.email_and_sms_service.RegistrationEmailDto;
import mutlu.ticketingapp.dto.user.*;
import mutlu.ticketingapp.entity.User;
import mutlu.ticketingapp.exception.FieldsDoesNotMatchException;
import mutlu.ticketingapp.exception.LoginException;
import mutlu.ticketingapp.exception.UserAlreadyExistException;
import mutlu.ticketingapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AmqpTemplate rabbitTemplate;

    Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AmqpTemplate rabbitTemplate) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Creates and saves to database a new User entity with it's password hashed with {@link PasswordEncoder}.
     *
     * @throws IllegalArgumentException if passwords does not match.
     */
    public GetUserDto create(CreateUserDto request) {
        User user = new User();
        if (userRepository.findUserByEmail(request.email()).isPresent()){
            throw new UserAlreadyExistException();
        }
        if (!request.firstPassword().equals(request.secondPassword())) {
            throw new IllegalArgumentException("Passwords does not match.");
        }
        user.setUserType(request.userType())
                .setEmail(request.email())
                .setPhoneNumber(request.phoneNumber())
                .setFirstName(request.firstName())
                .setLastName(request.lastName())
                .setUserType(request.userType())
                .setPasswordHash(passwordEncoder.encode(request.firstPassword()));
        log.info("Saving new user: {}", user);

        rabbitTemplate.convertAndSend(new RegistrationEmailDto(
                user.getEmail(), user.getFirstName(), user.getLastName()));

        return GetUserDto.fromUser(userRepository.save(user));
    }


    /**
     * Return User with given userId.
     *
     * @throws IllegalArgumentException if it does not exist.
     */
    public Optional<GetUserDto> getByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        log.debug("User for id {} is {}", userId, user.orElse(null));
        return Optional.of(GetUserDto.fromUser(user.orElseThrow(
                () -> new IllegalArgumentException("User with given id cannot be found."))));
    }

    /**
     * Mark User with given userId as deleted to be excluded from future queries.
     *
     * @throws IllegalArgumentException if it does not exist.
     */
    public void delete(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User with given id cannot be found.");
        }
        log.info("Marking user {} as deleted.", userOpt.get());
        userOpt.get().setDeleted(true);
        userRepository.flush();
    }

    /**
     * Tries to log user in, if successful returns that User entity.
     *
     * @throws LoginException
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
     * @throws FieldsDoesNotMatchException
     */
    public GetUserDto changeEmail(ChangeEmailDto request) {
        //Authenticate the user.
        var user = login(request.oldEmail(), request.password());
        if (!request.newEmailFirst().equals(request.newEmailSecond())) {
            throw new FieldsDoesNotMatchException("Email");
        }
        log.info("Changing email for {} to {}", user.getEmail(), request.newEmailFirst());
        user.setEmail(request.newEmailFirst());
        log.debug("Email before flush: {}", user.getEmail());
        userRepository.flush();
        log.debug("Email after flush: {}", user.getEmail());
        return GetUserDto.fromUser(user);
    }

    /**
     * Compares {@link ChangePasswordDto}'s newPassword field's hash values and logs user.
     * If both is successful changes user's passwordHash fiels to newPassword's hash value.
     *
     * @throws LoginException              .
     * @throws FieldsDoesNotMatchException .
     */
    public GetUserDto changePassword(ChangePasswordDto changePasswordDto) {
        //Authenticate the user.
        var user = login(changePasswordDto.email(), changePasswordDto.oldPassword());
        log.info("Changing password for {}: ", user.getEmail());
        if (changePasswordDto.newPasswordFirst().equals(changePasswordDto.newPasswordSecond())) {
            user.setPasswordHash(passwordEncoder.encode(changePasswordDto.newPasswordFirst()));
            log.debug("Password hash before flush {}",
                    userRepository.findById(user.getUserId()).get().getPasswordHash());
            userRepository.flush();
            log.debug("Password hash after flush {}",
                    userRepository.findById(user.getUserId()).get().getPasswordHash());
        } else {
            throw new FieldsDoesNotMatchException("Password");
        }
        return GetUserDto.fromUser(user);
    }

    public GetUserDto update(UpdateUserDto request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException());
        log.info("Updating user {}", user);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        return GetUserDto.fromUser(user);
    }
}
