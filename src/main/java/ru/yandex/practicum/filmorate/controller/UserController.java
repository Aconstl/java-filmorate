package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.customException.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users = new HashMap<>();
    private int idUser = 1;

    @GetMapping
    public List<User> getAllUsers() {
            log.trace("Вывод списка всех пользователей");
            List<User> usList = new ArrayList<>();
            users.forEach((key, value) -> usList.add(value));
            return usList;
    }

    @PostMapping
    public User addUser(@RequestBody User user) throws ValidationException {
        log.trace("Добавление нового пользователя");
        if (isValidate(user)) {
            if (user.getName() == null || user.getName().isBlank()) {
                log.warn("Пользователь id № {} не имеет имя, принят login пользователя \"{}\"", user.getId(), user.getLogin());
                user.setName(user.getLogin());
            }
            user.setId(idUser++);
            users.put(user.getId(), user);
            log.debug("Пользователь id № {} добавлен", user.getId());
        }
            return user;

    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws ValidationException {
        log.trace("Обновление пользователя");
        if (user.getId() == 0 || !users.containsKey(user.getId())) {
            log.error("Ошибка обновления id № {}", user.getId());
            throw new ValidationException(String.format("Пользователь c id № %d для обновления данных не найден",
                    user.getId()));
        }
        if (isValidate(user)) {
            if (user.getName() == null || user.getName().isBlank()) {
                log.warn("Пользователь id № {} не имеет имя, принят login пользователя \"{}\"", user.getId(), user.getLogin());
                user.setName(user.getLogin());
            }
            log.debug("Данные пользователя id № {} обновлены ", user.getId());
            users.put(user.getId(), user);
        }
        return user;
    }

    private boolean isValidate(User user) throws  ValidationException {
            if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
                log.error("Ошибка валидации id № {} : неккоректный email", user.getId());
                throw new ValidationException("Неверно введена электронная почта");
            }
            if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
                log.error("Ошибка валидации id № {} : неккоректный login", user.getId());
                throw new ValidationException("Неверно введен логин");
            }
            if (user.getBirthday().isAfter(LocalDate.now())) {
                log.error("Ошибка валидации id № {} : неккоректный birthday", user.getId());
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
            return true;
    }

}