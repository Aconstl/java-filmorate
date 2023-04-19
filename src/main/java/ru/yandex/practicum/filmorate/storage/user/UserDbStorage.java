package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customException.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Component
@Qualifier("dbUserStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAll() {
        log.trace("Вывод списка всех пользователей");

        return jdbcTemplate.query("SELECT * FROM users", (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User add(User user) {
        log.trace("Добавление нового пользователя");
        if (isValidate(user)) {
            if (user.getName() == null || user.getName().isBlank()) {
                log.warn("Пользователь {} не имеет имя, принят как login", user.getLogin());
                user.setName(user.getLogin());
            }

            jdbcTemplate.update("INSERT INTO USERS (EMAIL,LOGIN,NAME,BIRTHDAY) VALUES (?,?,?,?)",
                    user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

            user.setId(getMaxIdUser());
        }
        log.debug("Пользователь id № {} добавлен", user.getId());
        return get(user.getId());
    }

    @Override
    public User update(User user) {
        log.trace("Обновление пользователя");
        if (user.getId() == 0) {
            throw new IllegalArgumentException(String.format("Пользователь %s для обновления данных не найден",
                    user.getLogin()));
        }
        if (isValidate(user)) {
            if (user.getName() == null || user.getName().isBlank()) {
                log.warn("Пользователь {} не имеет имя, принят как login", user.getLogin());
                user.setName(user.getLogin());
            }

            jdbcTemplate.update("UPDATE USERS " +
                            "SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE USER_ID = ? ",
                    user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

            log.debug("Данные пользователя id № {} обновлены ", user.getId());
        }
        return get(user.getId());
    }

    @Override
    public User get(Integer id) {
        log.trace("Получение пользователя");
        if (id == null) {
            throw new NullPointerException("ID пользователя указан неверно");
        }
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE USER_ID=?", (rs, rowNum) -> makeUser(rs), id);
        if (users.size() == 1) {
            log.debug("Пользователь с id №{} получен", id);
            return users.get(0);
        } else if (users.isEmpty()) {
            throw new IllegalArgumentException("Пользователь c id № " + id + " не найден");
        } else {
            throw new ValidationException("Количество пользователей при вызове по id не должно превышать 1");
        }
    }

    @Override
    public List<User> getFriends(Integer id) {

        log.trace("Вывод друзей");

        String sql = "SELECT *\n" +
                "FROM USERS u " +
                "WHERE u.USER_ID IN (\n" +
                "\tSELECT USERSECOND_ID " +
                "\tFROM FRIENDS \n" +
                "\tWHERE USERFIRST_ID = ?) ";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
    }

    public User makeUser(ResultSet userRows) throws SQLException {
        User user = User.builder()
                .id(userRows.getInt("USER_ID"))
                .email(userRows.getString("EMAIL"))
                .login(userRows.getString("LOGIN"))
                .birthday(userRows.getObject("BIRTHDAY", LocalDate.class))
                .build();

        if (userRows.getString("NAME").isBlank()) {
            user.setName(user.getLogin());
        } else {
            user.setName(userRows.getString("NAME"));
        }

        //добавление списка друзей
        String sqlFriends = "SELECT USERSECOND_ID,IS_CONFIRM " +
                "\tFROM FRIENDS \n" +
                "\tWHERE USERFIRST_ID = ?";

        HashMap<Integer, Boolean> map = new HashMap<>();
        SqlRowSet rsMap = jdbcTemplate.queryForRowSet(sqlFriends, user.getId());
        while (rsMap.next()) {
            map.put(rsMap.getInt("USERSECOND_ID"), rsMap.getBoolean("IS_CONFIRM"));
        }
        user.setFriends(map);

        return user;
    }

    private int getMaxIdUser() {
        String sqlgetId = "SELECT USER_ID \n" +
                "FROM USERS u \n" +
                "ORDER BY USER_ID DESC \n" +
                "LIMIT 1";

        List<Integer> id = jdbcTemplate.query(sqlgetId, (rs, rowNum) -> rs.getInt("USER_ID"));
        return id.get(0);
    }

    private boolean isValidate(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем (пользователь № " + user.getId() + ")");
        }
        return true;
    }

    public void addFriends(Integer id, Integer friendId, boolean isMutually) {
        if (isMutually) {
            jdbcTemplate.update("DELETE FROM FRIENDS WHERE USERFIRST_ID = ? AND USERSECOND_ID = ?", friendId, id);
            String sqlAddFirst = "INSERT INTO FRIENDS (USERFIRST_ID,USERSECOND_ID,IS_CONFIRM)" +
                    "VALUES (?,?,TRUE),\n" +
                    "(?,?,TRUE);";
            jdbcTemplate.update(sqlAddFirst, id, friendId, friendId, id);
        } else {
            String sqlAddFirst = "INSERT INTO FRIENDS (USERFIRST_ID,USERSECOND_ID,IS_CONFIRM)" +
                    "VALUES (?,?,FALSE)";
            jdbcTemplate.update(sqlAddFirst, id, friendId);
        }
    }

    public void removeFriends(Integer id, Integer friendId) {
        jdbcTemplate.update("DELETE FROM FRIENDS WHERE USERFIRST_ID = ? AND USERSECOND_ID = ?", id, friendId);
        jdbcTemplate.update("DELETE FROM FRIENDS WHERE USERFIRST_ID = ? AND USERSECOND_ID = ?", friendId, id);
    }

    public List<User> getJointFriends(Integer id, Integer friendId) {
        String sql = "SELECT *\n" +
                "FROM USERS u \n" +
                "WHERE USER_ID IN (\n" +
                "SELECT f1.USERSECOND_ID \n" +
                "\tFROM FRIENDS f1 \n" +
                "\tINNER JOIN FRIENDS f2 ON f2.USERFIRST_ID = ? AND\n" +
                "\t\tf2.USERSECOND_ID = f1.USERSECOND_ID  \n" +
                "\t\tWHERE f1.USERFIRST_ID  = ? );";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id, friendId);
    }
}
