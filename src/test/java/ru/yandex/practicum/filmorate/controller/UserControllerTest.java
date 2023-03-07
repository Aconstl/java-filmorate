package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.customException.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTest {
    UserController userController;
    User user;
    @BeforeEach
    public void beforeAll() {
        userController = new UserController();
        user = User.builder()
                .email("aconstl@gmail.com")
                .login("mrAlex")
                .name("alexey")
                .birthday(LocalDate.of(1998,9,11))
                .build();
    }
    @Test
    public void emailUserTest() throws ValidationException {
        assertTrue(userController.getAllUsers().isEmpty());
        User userAddTrue = userController.addUser(user);
        assertEquals(userAddTrue, user);

        user.setEmail("aconstlgmail.com");
        ValidationException ve1 = assertThrows(ValidationException.class,
                () -> userController.addUser(user));
        assertEquals(ve1.getMessage(),"Неверно введена электронная почта");

        user.setEmail("");
        ValidationException ve2 = assertThrows(ValidationException.class,
                () -> userController.addUser(user));
        assertEquals(ve2.getMessage(),"Неверно введена электронная почта");
    }

    @Test
    public void loginUserTest() throws ValidationException {
        User userAddTrue = userController.addUser(user);
        assertEquals(userAddTrue, user);

        user.setLogin("mr Alex");
        ValidationException ve1 = assertThrows(ValidationException.class,
                () -> userController.addUser(user));
        assertEquals(ve1.getMessage(),"Неверно введен логин");

        user.setLogin("");
        ValidationException ve2 = assertThrows(ValidationException.class,
                () -> userController.addUser(user));
        assertEquals(ve2.getMessage(),"Неверно введен логин");
    }
    @Test
    public void birthdayUserTest() throws ValidationException {
        User userAddTrue = userController.addUser(user);
        assertEquals(userAddTrue, user);

        user.setBirthday(LocalDate.now());
        User userAddTrue1 = userController.addUser(user);
        assertEquals(userAddTrue1, user);

        user.setBirthday(LocalDate.now().plusDays(1));
        ValidationException ve1 = assertThrows(ValidationException.class,
                () -> userController.addUser(user));
        assertEquals(ve1.getMessage(),"Дата рождения не может быть в будущем");
    }
    @Test
    public void nameUserTest() throws ValidationException {
        User userAddTrue = userController.addUser(user);
        assertEquals(userAddTrue.getName(), user.getName());
        assertEquals(userAddTrue, user);

        user.setName("");
        User userAddTrue1 = userController.addUser(user);
        assertEquals(userAddTrue1.getName(), user.getLogin());

    }

}
