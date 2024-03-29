package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.customException.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {

    private final UserController userController;
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
                .friends(new HashMap<>())
                .birthday(LocalDate.of(1998, 9, 11))
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
        User userAddTrue = userController.add(user);
        assertEquals(userAddTrue, user);

        user.setBirthday(LocalDate.now());
        User userAddTrue1 = userController.add(user);
        assertEquals(userAddTrue1, user);

        user.setBirthday(LocalDate.now().plusDays(1));
        ValidationException ve1 = assertThrows(ValidationException.class,
                () -> userController.add(user));
        assertEquals(ve1.getMessage(), "Дата рождения не может быть в будущем (пользователь № " + user.getId() + ")");
    }

    @Test
    public void nameUserTest() throws ValidationException {
        User userAddTrue = userController.add(user);
        assertEquals(userAddTrue.getName(), user.getName());
        assertEquals(userAddTrue, user);

        user.setName("");
        User userAddTrue1 = userController.add(user);
        assertEquals(userAddTrue1.getName(), user.getLogin());
    }

    @Test
    public void testUser() {
        User user = userController.add(User.builder()
                .name("Пользователь1")
                .email("mail@mail.ru")
                .login("login1")
                .birthday(LocalDate.of(1895, 12, 28))
                .build());

        assertThat(user.toString(), containsString("Пользователь1"));

        User film1 = userController.get(user.getId());
        assertThat(film1.toString(), containsString("Пользователь1"));

        List<User> users = userController.getAll();
        assertThat(users.toString(), containsString("Пользователь1"));


        User userUPD = userController.update(User.builder()
                .id(1)
                .name("ПользовательОбновленный")
                .email("mail@mail.ru")
                .login("login1")
                .birthday(LocalDate.of(1895, 12, 28))
                .build());

        assertThat(userUPD.toString(), containsString("ПользовательОбновленный"));
    }
}
