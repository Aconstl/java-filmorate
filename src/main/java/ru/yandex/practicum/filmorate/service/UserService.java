package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customException.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserStorage userStorage){
        this.userStorage = userStorage;
    }

    public String addFriends(Integer id, Integer friendId) {
        log.trace("Добавление в друзья");
        User user1 = userStorage.get(id);
        User user2 = userStorage.get(friendId);
        if (isNotEquals(user1,user2)) {
            user1.getFriends().add(user2.getId());
            user2.getFriends().add(user1.getId());
            log.debug("Пользователи с id №{} и №{} стали друзьями", id,friendId);
        }
        return "стали друзьями";
    }

    public String removeFriend(Integer id, Integer friendId) {
        log.trace("Удаление из друзей");
        User user1 = userStorage.get(id);
        User user2 = userStorage.get(friendId);
        if (isNotEquals(user1,user2)) {
            user1.getFriends().remove(user2.getId());
            user2.getFriends().remove(user1.getId());
            log.debug("Пользователи с id №{} и №{} перестали быть друзьями", id,friendId);
        }
        return "перестали быть друзьями";
    }

    public List<User> getJointFriends(Integer id, Integer friendId) {
        log.trace("Вывод общих друзей");
        User user1 = userStorage.get(id);
        User user2 = userStorage.get(friendId);
        List<User> JointFriends = new ArrayList<>();
        if (isNotEquals(user1,user2)) {
            Set<Integer> joint = new HashSet<>(user1.getFriends());
            Set<Integer> jointFriend = user2.getFriends();
            joint.retainAll(jointFriend);
            for (Integer idUser : joint) {
                JointFriends.add(userStorage.get(idUser));
            }
            log.debug("Выведены общие друзья пользователей с id №{} и №{}", id,friendId);
        }
        return JointFriends;
    }

    private boolean isNotEquals(User user1,User user2) {
        if (user1.equals(user2)) {
            log.error("Ошибка валидации : пользователь пыается совершить действие с одним пользователем");
            throw new ValidationException("Пользователь не может совершать действие с самим собой");
        }
        return true;
    }
}
