package ru.yandex.practicum.filmorate.service.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    Logger log = LoggerFactory.getLogger(UserService.class);

    List<Integer> addFriends(Integer id, Integer friendId);
    List<Integer> removeFriend(Integer id, Integer friendId);
    List<User> getJointFriends(Integer id, Integer friendId);
}
