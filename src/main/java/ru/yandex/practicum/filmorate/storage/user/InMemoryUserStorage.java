package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.customException.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users = new HashMap<>();

    private int idUser = 1;

    @Override
    public List<User> getAll() {
        log.trace("Вывод списка всех пользователей");
        List<User> usList = new ArrayList<>();
        users.forEach((key, value) -> usList.add(value));
        return usList;
    }

    @Override
    public User add(User user) {
        log.trace("Добавление нового пользователя");
        if (isValidate(user)) {
            if (user.getName() == null || user.getName().isBlank()) {
                log.warn("Пользователь id № {} не имеет имя, принят login пользователя \"{}\"",
                        user.getId(), user.getLogin());
                user.setName(user.getLogin());
            }
            if (user.getFriends() == null) {
                log.warn("Пользователь id № {} не имеет список друзей ", user.getId());
                user.setFriends(new HashSet<>());
            }
            user.setId(idUser++);
            users.put(user.getId(), user);
            log.debug("Пользователь id № {} добавлен", user.getId());
        }
        return user;
    }

    @Override
    public User update(User user) {
        log.trace("Обновление пользователя");
        if (user.getId() == 0 || !users.containsKey(user.getId())) {
            log.error("Ошибка обновления id № {}", user.getId());
            throw new IllegalArgumentException(String.format("Пользователь c id № %d для обновления данных не найден",
                    user.getId()));
        }
        if (isValidate(user)) {
            if (user.getName() == null || user.getName().isBlank()) {
                log.warn("Пользователь id № {} не имеет имя, принят login пользователя \"{}\"", user.getId(), user.getLogin());
                user.setName(user.getLogin());
            }
            if (user.getFriends() == null) {
                log.warn("Пользователь id № {} не имеет список друзей ", user.getId());
                user.setFriends(new HashSet<>());
            }
            users.put(user.getId(), user);
            log.debug("Данные пользователя id № {} обновлены ", user.getId());
        }
        return user;
    }

    @Override
    public User get (Integer id){
        log.trace("Получение пользователя");
        if (id == null) {
            throw new NullPointerException("ID пользователя указан неверно");
        } else if (!users.containsKey(id)){
            throw new IllegalArgumentException("Пользователь не найден");
        }
        log.debug("Пользователь с id №{} получен", users.get(id).getName());
        return  users.get(id);
    }

    @Override
    public List<User> getFriends(Integer id){
        log.trace("Вывод общих друзей");
        User user = this.get(id);
        List <Integer> listIdFriends = new ArrayList<>(user.getFriends());
        List<User> listFriends = new ArrayList<>();
        for (Integer idFriend : listIdFriends) {
            if (!users.containsKey(idFriend)) {
                throw new IllegalArgumentException("Пользователь с id "+ idFriend + "имеется в списпе друзей, но не в общем списке");
            }
            listFriends.add(users.get(idFriend));
        }
        return listFriends;
    }

    private boolean isValidate(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации id № {} : неккоректный birthday", user.getId());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        return true;
    }

}

