package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customException.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.ArrayList;
import java.util.List;

@Service ("dbUserService")
@RequiredArgsConstructor
public class UserDbService implements UserService {

    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    public List<Integer> addFriends(Integer id, Integer friendId) {
        log.trace("Добавление в друзья");
        User user1 = userStorage.get(id);
        User user2 = userStorage.get(friendId);
        if (isNotEquals(user1.getId(),user2.getId())) {
            if (user1.getFriends().containsKey(user2.getId())) {
                throw new IllegalArgumentException(String.format("Пользователь %s уже имеет в списке друзей %s",
                        user1.getLogin(),user2.getLogin()));
            }
            if (user2.getFriends().containsKey(id)) {
                jdbcTemplate.update("DELETE FROM FRIENDS WHERE USERFIRST_ID = ? AND USERSECOND_ID = ?",friendId,id);
                String sqlAddFirst = "INSERT INTO FRIENDS (USERFIRST_ID,USERSECOND_ID,IS_CONFIRM)" +
                        "VALUES (?,?,TRUE),\n" +
                        "(?,?,TRUE);";
                jdbcTemplate.update(sqlAddFirst, id,friendId, friendId,id);

                log.debug("Пользователи с id №{} и №{} стали друзьями", id,friendId);
            } else {
                String sqlAddFirst = "INSERT INTO FRIENDS (USERFIRST_ID,USERSECOND_ID,IS_CONFIRM)" +
                        "VALUES (?,?,FALSE)";
                jdbcTemplate.update(sqlAddFirst, id,friendId);
                log.debug("Пользователь с id №{} добавил в список друзей пользователя №{}", id, friendId);
            }
        }
        return new ArrayList<>(user1.getFriends().keySet());
    }

    public List<Integer> removeFriend(Integer id, Integer friendId) {
        log.trace("Удаление из друзей");

        jdbcTemplate.update("DELETE FROM FRIENDS WHERE USERFIRST_ID = ? AND USERSECOND_ID = ?",id,friendId);
        jdbcTemplate.update("DELETE FROM FRIENDS WHERE USERFIRST_ID = ? AND USERSECOND_ID = ?",friendId,id);

        log.debug("Пользователи с id №{} и №{} перестали быть друзьями", id,friendId);
        return new ArrayList<>(userStorage.get(id).getFriends().keySet());
    }

    public List<User> getJointFriends(Integer id, Integer friendId) {
            log.trace("Вывод общих друзей");
            String sql = "SELECT *\n" +
                    "FROM USERS u \n" +
                    "WHERE USER_ID IN (\n" +
                    "SELECT f1.USERSECOND_ID \n" +
                    "\tFROM FRIENDS f1 \n" +
                    "\tINNER JOIN FRIENDS f2 ON f2.USERFIRST_ID = ? AND\n" +
                    "\t\tf2.USERSECOND_ID = f1.USERSECOND_ID  \n" +
                    "\t\tWHERE f1.USERFIRST_ID  = ? );";

            List<User> joinFriends = jdbcTemplate.query(sql,(rs, rowNum) -> userStorage.makeUser(rs),id,friendId);
            log.debug("Выведены общие друзья пользователей с id №{} и №{}", id,friendId);
            return joinFriends;
    }



    private boolean isNotEquals(Integer id1, Integer id2) {
        if (id1.equals(id2)) {
            throw new ValidationException("Пользователь не может совершать действие с самим собой (id № "+ id1 + " )");
        }
        return true;
    }

}
