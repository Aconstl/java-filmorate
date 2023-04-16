package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    Logger log = LoggerFactory.getLogger(UserStorage.class);

    List<User> getAll();

    User add(User user);

    User update(User user);

    User get(Integer id);

    List<User> getFriends(Integer id);
}
