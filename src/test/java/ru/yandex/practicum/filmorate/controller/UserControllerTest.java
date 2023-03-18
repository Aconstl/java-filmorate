package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.customException.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTest {

    private Validator validator;
    User user;
    @BeforeEach
    public void beforeAll() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        user = User.builder()
                .email("aconstl@gmail.com")
                .login("mrAlex")
                .name("alexey")
                .birthday(LocalDate.of(1998,9,11))
                .build();
    }

    @Test
    public void emailUserTest() throws ValidationException {
        Set<ConstraintViolation<User>> violation = validator.validate(user);
        assertTrue(violation.isEmpty());

        user.setEmail("aconstl");
        Set<ConstraintViolation<User>> violationFalse1 = validator.validate(user);
        assertFalse(violationFalse1.isEmpty());

        user.setEmail("");
        Set<ConstraintViolation<User>> violationFalse2 = validator.validate(user);
        assertFalse(violationFalse2.isEmpty());
    }

    @Test
    public void loginUserTest() throws ValidationException {
        Set<ConstraintViolation<User>> violation = validator.validate(user);
        assertTrue(violation.isEmpty());

        user.setLogin("");
        Set<ConstraintViolation<User>> violationFalse1 = validator.validate(user);
        assertFalse(violationFalse1.isEmpty());
    }

    @Test
    public void birthdayUserTest() throws ValidationException {

        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        UserController userController = new UserController(userStorage,userService);


        User userAddTrue = userController.add(user);
        assertEquals(userAddTrue, user);

        user.setBirthday(LocalDate.now());
        User userAddTrue1 = userController.add(user);
        assertEquals(userAddTrue1, user);

        user.setBirthday(LocalDate.now().plusDays(1));
        ValidationException ve1 = assertThrows(ValidationException.class,
                () -> userController.add(user));
        assertEquals(ve1.getMessage(),"Дата рождения не может быть в будущем");
    }

    @Test
    public void nameUserTest() throws ValidationException {

        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        UserController userController = new UserController(userStorage,userService);

        User userAddTrue = userController.add(user);
        assertEquals(userAddTrue.getName(), user.getName());
        assertEquals(userAddTrue, user);

        user.setName("");
        User userAddTrue1 = userController.add(user);
        assertEquals(userAddTrue1.getName(), user.getLogin());
    }

}
