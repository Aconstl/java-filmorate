package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customException.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Qualifier("memoryUserService")
public class InMemoryUserService implements UserService {
    private final UserStorage userStorage;


    @Autowired
    public InMemoryUserService(@Qualifier("memoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<Integer> addFriends(Integer id, Integer friendId) {
        log.trace("Добавление в друзья");
        User user1 = userStorage.get(id);
        User user2 = userStorage.get(friendId);
        if (isNotEquals(user1, user2)) {
            boolean isMutually = false;
            if (user2.getFriends().containsKey(user1.getId())) {
                user2.getFriends().put(user1.getId(), true);
                isMutually = true;
            }
            user1.getFriends().put(user2.getId(), isMutually);

            log.debug("Пользователи с id №{} и №{} стали друзьями", id, friendId);
        }
        return new ArrayList<>(user1.getFriends().keySet());
    }

    public List<Integer> removeFriend(Integer id, Integer friendId) {
        log.trace("Удаление из друзей");
        User user1 = userStorage.get(id);
        User user2 = userStorage.get(friendId);
        if (isNotEquals(user1, user2)) {
            user1.getFriends().remove(user2.getId());
            user2.getFriends().remove(user1.getId());
            log.debug("Пользователи с id №{} и №{} перестали быть друзьями", id, friendId);
        }
        return new ArrayList<>(user1.getFriends().keySet());
    }

    public List<User> getJointFriends(Integer id, Integer friendId) {
        log.trace("Вывод общих друзей");
        User user1 = userStorage.get(id);
        User user2 = userStorage.get(friendId);
        List<User> JointFriends = new ArrayList<>();
        if (isNotEquals(user1, user2)) {
            List<Integer> joint = user1.getFriends().entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            List<Integer> jointFriend = user2.getFriends().entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            joint.retainAll(jointFriend);
            for (Integer idUser : joint) {
                JointFriends.add(userStorage.get(idUser));
            }
            log.debug("Выведены общие друзья пользователей с id №{} и №{}", id, friendId);
        }
        return JointFriends;
    }

    private boolean isNotEquals(User user1, User user2) {
        if (user1.equals(user2)) {
            throw new ValidationException("Пользователь не может совершать действие с самим собой (id № " + user1.getId() + " )");
        }
        return true;
    }
}
