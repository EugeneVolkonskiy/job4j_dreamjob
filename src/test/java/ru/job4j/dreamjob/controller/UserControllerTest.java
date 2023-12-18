package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private UserService userService;
    private UserController userController;
    private HttpServletRequest request;
    private HttpSession session;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
        request = new MockHttpServletRequest();
        session = new MockHttpSession();
    }

    @Test
    public void whenRequestRegistrationThanGetRegistrationPage() {
        var view = userController.getRegistrationPage();
        assertThat(view).isEqualTo("users/register");
    }

    @Test
    public void whenRegistrationSuccessfullyThanRedirectToVacanciesPage() {
        var user = new User(1, "test@mail.ru", "Ivan", "123");
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.register(user, model);
        var actualUser = userArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualUser).isEqualTo(user);
    }

    @Test
    public void whenNotRegisteredThanGetErrorMessage() {
        var user = new User(1, "test@mail.ru", "Ivan", "123");
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(Optional.empty());
        var expectedMessage = String.format("Пользователь с почтой %s уже существует", user.getEmail());

        var model = new ConcurrentModel();
        var view = userController.register(user, model);
        var actualMessage = model.getAttribute("message");
        var actualUser = userArgumentCaptor.getValue();

        assertThat(view).isEqualTo("users/register");
        assertThat(actualUser).isEqualTo(user);
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void whenRequestLoginPageThanGetLoginPage() {
        var view = userController.getLoginPage();
        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenLoginUserSuccessfullyThanRedirectToVacanciesPage() {
        var user = new User(1, "test@mail.ru", "Ivan", "123");
        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, request);

        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenLoginUserFailedThanGetErrorMessage() {
        var user = new User(1, "test@mail.ru", "Ivan", "123");
        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword())).thenReturn(Optional.empty());
        var expectedMessage = "Почта или пароль введены неверно";

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, request);
        var actualMessage = model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void whenLogoutRequestThenRedirectToLoginPage() {
        var view = userController.logout(session);
        assertThat(view).isEqualTo("redirect:/users/login");
    }
}