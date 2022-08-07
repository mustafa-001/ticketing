package mutlu.ticketingapp.service;


import mutlu.ticketingapp.dto.user.*;
import mutlu.ticketingapp.entity.User;
import mutlu.ticketingapp.enums.UserType;
import mutlu.ticketingapp.exception.FieldsDoesNotMatchException;
import mutlu.ticketingapp.exception.LoginException;
import mutlu.ticketingapp.exception.UserAlreadyExistException;
import mutlu.ticketingapp.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;


@SpringBootTest
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private User user;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;


    @BeforeEach
    void init() {
        user.setEmail("aaa@bbb.com");
    }

    @Test
    void shouldThrowUserAlreadyExistsExceptionWhenEmailIsUsed() {
        when(userRepository.findUserByEmail(Mockito.any())).thenReturn(Optional.of(user));
        CreateUserDto userDto = new CreateUserDto(UserType.CORPORATE, "abc@xyz.com", "5554443322",
                "aa", "bb", "password", "differentPassword");

        Throwable ex = catchThrowable(() -> userService.create(userDto));

        assertThat(ex).isInstanceOf(UserAlreadyExistException.class);
    }

    @Test
    void shouldThrowExceptionWhenPasswordsDoesNotMatch() {
        CreateUserDto userDto = new CreateUserDto(UserType.CORPORATE, "abc@xyz.com", "5554443322",
                "aa", "bb", "password", "differentPassword");

        Throwable exception = catchThrowable(() -> userService.create(userDto));
        assertThat(exception).isInstanceOf(FieldsDoesNotMatchException.class);
    }

    @Test
    void shouldSendEmailToQueueWhenRegistrySuccessful() {
        CreateUserDto userDto = new CreateUserDto(UserType.CORPORATE, "abc@xyz.com", "5554443322",
                "aa", "bb", "password", "password");
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(new User());
        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn("");

        userService.create(userDto);

        verify(rabbitTemplate, times(1)).convertAndSend(Mockito.any());
        verify(userRepository, times(1)).save(Mockito.any());
    }

    @Test
    void shouldEncyrptPasswordWhenRegistring() {
        CreateUserDto userDto = new CreateUserDto(UserType.CORPORATE, "abc@xyz.com", "5554443322",
                "aa", "bb", "password", "password");
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(new User());
        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn("hashedPassword");

        userService.create(userDto);

        verify(passwordEncoder, times(1)).encode(Mockito.any());
        verify(userRepository)
                .save(argThat(x -> !x.getPasswordHash().equals(userDto.firstPassword())));
        verify(userRepository)
                .save(argThat(x -> !x.getPasswordHash().equals(userDto.secondPassword())));
        verify(userRepository)
                .save(argThat(x -> !x.getPasswordHash().isEmpty()));
    }

    @Test
    void shouldThrowInvalidParameterWhenInvalidUserIdIsGiven() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> userService.getByUserId(1L));

        assertThat(ex).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void shouldCallUpdateDeletedFieldWithNoOtherModification() {
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        userService.delete(100L);

        verify(user, times(1)).setDeleted(true);
        verify(userRepository, times(0)).deleteById(Mockito.any());
        verify(userRepository, times(0)).delete(Mockito.any());
    }


    @Test
    void shouldThrowLoginExceptionWhenUserWithEmailDoesNotExist() {
        when(userRepository.findUserByEmail("aaa@bbb.com")).thenReturn(Optional.empty());
        LoginCredentialsDto loginCredentialsDto = new LoginCredentialsDto("aaa@bbb.com", "123456");

        Throwable ex = catchThrowable(() -> userService.login(loginCredentialsDto));

        assertThat(ex).isInstanceOf(LoginException.class);
    }

    @Test
    void shouldThrowLoginExceptionWhenPasswordsDoesNotMatch() {
        when(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(false);
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        ChangePasswordDto changePasswordDto = new ChangePasswordDto("aa@bb.email", "123456", "1111", "1111");

        Throwable ex = catchThrowable(() -> userService.changePassword(changePasswordDto));

        assertThat(ex).isInstanceOf(LoginException.class);
    }

    @Test
    void shouldCallPasswordEncoderMatchesWithCorrectArguments() {
        when(user.getPasswordHash()).thenReturn("somePasswordHash");
        when(userRepository.findUserByEmail(Mockito.any())).thenReturn(Optional.of(user));
        LoginCredentialsDto loginCredentials =
                new LoginCredentialsDto(Mockito.any(), "somePassword");

        catchThrowable(() -> userService.login(loginCredentials));

        verify(passwordEncoder).matches("somePassword", "somePasswordHash");
    }

    @Test
    void shouldReturnUserDtoWhenCorrectArgumentsGiven() {
        LoginCredentialsDto loginCredentialsDto = new LoginCredentialsDto("aaa@bbb.com", "123123");
        User user = new User();
        user.setFirstName("Java");
        when(userRepository.findUserByEmail("aaa@bbb.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(true);

        GetUserDto userDto = userService.login(loginCredentialsDto);

        Assertions.assertEquals(userDto.firstName(), user.getFirstName());
    }

    @Test
    void shouldThrowFieldsDoesNotMatchWhenCalledWithWrongArguments() {
        ChangeEmailDto changeEmailDto =
                new ChangeEmailDto("", "new@email.com", "moreNew@email.com", "");
        User user = new User();
        when(userRepository.findUserByEmail(Mockito.any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(true);

        Throwable ex = catchThrowable(() -> userService.changeEmail(changeEmailDto));

        assertThat(ex).isInstanceOf(FieldsDoesNotMatchException.class);
    }

    @Test
    void shouldChangeEmailWhenCalledWithCorrectArguments() {
        ChangeEmailDto changeEmailDto =
                new ChangeEmailDto("", "new@email.com", "new@email.com", "");
        User user = new User();
        when(userRepository.findUserByEmail(Mockito.any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(true);

        GetUserDto userDto = userService.changeEmail(changeEmailDto);

        Assertions.assertEquals(userDto.email(), changeEmailDto.newEmailFirst());
        Assertions.assertEquals(userDto.email(), changeEmailDto.newEmailSecond());
    }


    @Test
    void changePasswordShouldThrowFieldsDoesNotMatchWhenCalledWithWrongArguments() {
        ChangePasswordDto changePasswordDto =
                new ChangePasswordDto("", "", "newPassword", "newPasswordTypo");
        User user = new User();
        when(userRepository.findUserByEmail(Mockito.any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(true);

        Throwable ex = catchThrowable(() -> userService.changePassword(changePasswordDto));

        assertThat(ex).isInstanceOf(FieldsDoesNotMatchException.class);
    }

    @Test
    void shouldChangePasswordWhenCalledWithCorrectArguments() {
        ChangePasswordDto changePasswordDto =
                new ChangePasswordDto("", "", "newPassword", "newPassword");
        when(userRepository.findUserByEmail(Mockito.any())).thenReturn(Optional.of(user));
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(true);
        when(passwordEncoder.encode(Mockito.any())).thenReturn("passwordHash");

        userService.changePassword(changePasswordDto);

        verify(user).setPasswordHash("passwordHash");
    }

    @Test
    void shouldUpdateFirstNameAndSecondName() {
        UpdateUserDto updateUserDto = new UpdateUserDto(1L, "name", "surname");
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        userService.update(updateUserDto);

        verify(user, times(1)).setFirstName(updateUserDto.firstName());
        verify(user, times(1)).setLastName(updateUserDto.lastName());
    }

    @Test
    void shouldThrowInvalidParameterWhenInvalidUserIdIsGivenToUpdate() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> userService.update(new UpdateUserDto(1L, "", "")));

        assertThat(ex).isInstanceOf(IllegalArgumentException.class);
    }

}
