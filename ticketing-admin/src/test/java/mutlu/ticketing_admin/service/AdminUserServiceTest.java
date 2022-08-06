package mutlu.ticketing_admin.service;


import mutlu.ticketing_admin.dto.*;
import mutlu.ticketing_admin.entity.AdminUser;
import mutlu.ticketing_admin.exception.FieldsDoesNotMatchException;
import mutlu.ticketing_admin.exception.LoginException;
import mutlu.ticketing_admin.repository.AdminUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.InvalidParameterException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;


@SpringBootTest
class AdminUserServiceTest {

    @InjectMocks
    private AdminUserService adminUserService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private AdminUser adminUser;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AdminUserRepository adminUserRepository;


    @BeforeEach
    void init() {
        adminUser.setEmail("aaa@bbb.com");
    }

    @Test
    void shouldThrowExceptionWhenPasswordsDoesNotMatch() {
        CreateAdminUserDto userDto = new CreateAdminUserDto("abc@xyz.com",
                "aa", "bb", "password", "differentPassword");

        Throwable exception = catchThrowable(() -> adminUserService.create(userDto));
        assertThat(exception instanceof FieldsDoesNotMatchException);
    }

    @Test
    void shouldSendEmailToQueueWhenRegistrySuccessful() {
        CreateAdminUserDto userDto = new CreateAdminUserDto("abc@xyz.com",
                "aa", "bb", "password", "password");
        Mockito.when(adminUserRepository.save(Mockito.any())).thenReturn(new AdminUser());
        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn("");

        adminUserService.create(userDto);

        verify(rabbitTemplate, times(1)).convertAndSend(Mockito.any());
        verify(adminUserRepository, times(1)).save(Mockito.any());
    }

    @Test
    void shouldEncyrptPasswordWhenRegistry() {
        CreateAdminUserDto userDto = new CreateAdminUserDto("abc@xyz.com",
                "aa", "bb", "password", "password");
        Mockito.when(adminUserRepository.save(Mockito.any())).thenReturn(new AdminUser());
        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn("hashedPassword");

        adminUserService.create(userDto);

        verify(passwordEncoder, times(1)).encode(Mockito.any());
        verify(adminUserRepository)
                .save(argThat(x -> !x.getPasswordHash().equals(userDto.firstPassword())));
        verify(adminUserRepository)
                .save(argThat(x -> !x.getPasswordHash().equals(userDto.secondPassword())));
        verify(adminUserRepository)
                .save(argThat(x -> !x.getPasswordHash().isEmpty()));
    }

    @Test
    void shouldThrowInvalidParameterWhenInvalidUserIdIsGiven() {
        Mockito.when(adminUserRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> adminUserService.getByUserId(1L));

        assertThat(ex instanceof InvalidParameterException);

    }

    @Test
    void shouldCallUpdateDeletedFieldWithNoOtherModification() {
        when(adminUserRepository.findById(Mockito.any())).thenReturn(Optional.of(adminUser));

        adminUserService.delete(100L);

        verify(adminUser, times(1)).setDeleted(true);
        verify(adminUserRepository, times(0)).deleteById(Mockito.any());
        verify(adminUserRepository, times(0)).delete(Mockito.any());
    }


    @Test
    void shouldThrowLoginExceptionWhenUserWithEmailDoesNotExist() {
        when(adminUserRepository.findUserByEmail("aaa@bbb.com")).thenReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> adminUserService.getByUserId(1L));

        assertThat(ex instanceof LoginException);
    }

    @Test
    void shouldThrowLoginExceptionWhenPasswordsDoesNotMatch() {
        when(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(false);

        Throwable ex = catchThrowable(() -> adminUserService.getByUserId(1L));

        assertThat(ex instanceof LoginException);
    }

    @Test
    void shouldCallPasswordEncoderMatchesWithCorrectArguments() {
        when(adminUser.getPasswordHash()).thenReturn("somePasswordHash");
        when(adminUserRepository.findUserByEmail(Mockito.any())).thenReturn(Optional.of(adminUser));
        LoginCredentialsDto loginCredentials =
                new LoginCredentialsDto(Mockito.any(), "somePassword");

        catchThrowable(() -> adminUserService.login(loginCredentials));

        verify(passwordEncoder).matches("somePassword", "somePasswordHash");
    }

    @Test
    void shouldReturnUserDtoWhenCorrectArgumentsGiven() {
        LoginCredentialsDto loginCredentialsDto = new LoginCredentialsDto("aaa@bbb.com", "123123");
        AdminUser user = new AdminUser();
        user.setFirstName("Java");
        when(adminUserRepository.findUserByEmail("aaa@bbb.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(true);

        GetAdminUserDto userDto = adminUserService.login(loginCredentialsDto);

        Assertions.assertEquals(userDto.firstName(), user.getFirstName());
    }

    @Test
    void shouldThrowFieldsDoesNotMatchWhenCalledWithWrongArguments() {
        ChangeEmailDto changeEmailDto =
                new ChangeEmailDto("", "new@email.com", "moreNew@email.com", "");
        AdminUser user = new AdminUser();
        when(adminUserRepository.findUserByEmail(Mockito.any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(true);

        Throwable ex = catchThrowable(() -> adminUserService.changeEmail(changeEmailDto));

        assertThat(ex instanceof FieldsDoesNotMatchException);
    }

    @Test
    void shouldChangeEmailWhenCalledWithCorrectArguments() {
        ChangeEmailDto changeEmailDto =
                new ChangeEmailDto("", "new@email.com", "new@email.com", "");
        AdminUser user = new AdminUser();
        when(adminUserRepository.findUserByEmail(Mockito.any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(true);

        GetAdminUserDto userDto = adminUserService.changeEmail(changeEmailDto);

        Assertions.assertEquals(userDto.email(), changeEmailDto.newEmailFirst());
        Assertions.assertEquals(userDto.email(), changeEmailDto.newEmailSecond());
    }


    @Test
    void changePasswordShouldThrowFieldsDoesNotMatchWhenCalledWithWrongArguments() {
        ChangePasswordDto changePasswordDto =
                new ChangePasswordDto("", "", "newPassword", "newPasswordTypo");
        AdminUser user = new AdminUser();
        when(adminUserRepository.findUserByEmail(Mockito.any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(true);

        Throwable ex = catchThrowable(() -> adminUserService.changePassword(changePasswordDto));

        assertThat(ex instanceof FieldsDoesNotMatchException);
    }

    @Test
    void shouldChangePasswordWhenCalledWithCorrectArguments() {
        ChangePasswordDto changePasswordDto =
                new ChangePasswordDto("", "", "newPassword", "newPassword");
        when(adminUserRepository.findUserByEmail(Mockito.any())).thenReturn(Optional.of(adminUser));
        when(adminUserRepository.findById(Mockito.any())).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(true);
        when(passwordEncoder.encode(Mockito.any())).thenReturn("passwordHash");

        adminUserService.changePassword(changePasswordDto);

        verify(adminUser).setPasswordHash("passwordHash");
    }
}