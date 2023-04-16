package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.ArrayList;
import java.util.List;

@Service ("dbFilmService")
@RequiredArgsConstructor
public class FilmDBService implements  FilmService {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Integer> addLike(Integer idFilm, Integer idUser) {
        log.trace("Добавление лайка");
        Film film = filmStorage.get(idFilm);

        if (isValid(idUser)) {
            //КОД ДОБАВЛЕНИЯ В БД
            jdbcTemplate.update("INSERT INTO LIKES (FILM_ID,USER_ID) VALUES (?,?)",idFilm,idUser);

            log.debug("Пользователь с id №{} поставил лайк фильму \"{}\" (id№ {})", idUser,film.getName(),idFilm);
        }
        return new ArrayList<>(film.getLikes());
    }

    @Override
    public List<Integer> removeLike(Integer idFilm, Integer idUser) {
        log.trace("Удаление лайка");
        Film film = filmStorage.get(idFilm);
        if (isValid(idUser)) {
            //КОД УДАЛЕНИЯ В БД
            jdbcTemplate.update( "DELETE FROM LIKES WHERE FILM_ID =? AND USER_ID =?", idFilm,idUser);

            log.debug("Пользователь с id №{} убрал лайк фильму \"{}\" (id№ {})", idUser,film.getName(),idFilm);
        }
        return new ArrayList<>(film.getLikes());
    }

    @Override
    public List<Film> bestFilms(Integer count) {
        log.trace("Вывод лучших фильмов");
        //Код лучших фильмов
        String sql = " SELECT f.*\n" +
                "        FROM FILMS f\n" +
                "        LEFT OUTER JOIN likes AS l ON f.FILM_ID = l.FILM_ID\n" +
                "        GROUP BY f.FILM_ID\n" +
                "        ORDER BY COUNT(l.USER_ID) DESC,\n" +
                "        f.FILM_ID " +
                "        LIMIT ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> filmStorage.makeFilm(rs), count);
    }

    private boolean isValid(Integer idUser) {
        userStorage.get(idUser);
        return true;
    }


}
