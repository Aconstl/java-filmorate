package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customException.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.mpa.DbMpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("dbFilmStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getAll() {
        log.trace("Вывод списка всех фильмов");
        return jdbcTemplate.query("SELECT * FROM films", (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film add(Film film) {
        log.trace("Добавление нового фильма");
        if (isValidate(film)) {
            if (film.getLikes() == null) {
                film.setLikes(new HashSet<>());
            }

            jdbcTemplate.update("INSERT INTO FILMS (NAME,DESCRIPRION,RELEASEDATA,DURATION,MPA) " +
                            "VALUES (?,?,?,?,?)", film.getName(), film.getDescription(),
                    film.getReleaseDate(), film.getDuration(), film.getMpa().getId());

            film.setId(getMaxIdFilm());
            if (film.getGenres() != null) {
                connectFilmGenre(film);
            } else {
                film.setGenres(new HashSet<>());
            }
        }
        log.debug("Фильм \"{}\" (id №{}) добавлен", film.getName(), film.getId());
        return get(film.getId());
    }

    @Override
    public Film update(Film film) {
        log.trace("Обновление фильма");
        if (film.getId() == 0) {
            throw new IllegalArgumentException("Фильм c id № " + 0 + " не существует");
        } else if (get(film.getId()) != null && isValidate(film)) {

            disconnectFilmGenre(film);

            jdbcTemplate.update("UPDATE FILMS " +
                            "SET NAME = ?, DESCRIPRION = ?, RELEASEDATA = ?, DURATION = ?, MPA = ? WHERE FILM_ID = ? ",
                    film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                    film.getMpa().getId(), film.getId());

            if (film.getGenres() != null) {
                connectFilmGenre(film);
            } else {
                film.setGenres(new HashSet<>());
            }
        }
        log.debug("Фильм \"{}\" (id №{}) обновлен", film.getName(), film.getId());
        return get(film.getId());
    }

    @Override
    public Film get(Integer id) {
        log.trace("Получение фильма");
        if (id == null) {
            throw new NullPointerException("ID фильма указан неверно");
        }
        List<Film> films = jdbcTemplate.query("SELECT * FROM films WHERE FILM_ID=?", (rs, rowNum) -> makeFilm(rs), id);
        if (films.size() == 1) {
            log.debug("Фильм с id №{} получен", id);
            return films.get(0);
        } else if (films.isEmpty()) {
            throw new IllegalArgumentException("Фильм c id № " + id + " не найден");
        } else {
            throw new ValidationException("Количество фильмов при вызове по id не должно превышать 1");
        }
    }


    public Film makeFilm(ResultSet filmRows) throws SQLException {
        Film film = Film.builder()
                .id(filmRows.getInt("FILM_ID"))
                .name(filmRows.getString("NAME"))
                .description(filmRows.getString("DESCRIPRION"))
                .releaseDate(filmRows.getObject("RELEASEDATA", LocalDate.class))
                .duration(filmRows.getLong("DURATION"))
                .mpa(new DbMpa(jdbcTemplate).get((filmRows.getInt("MPA"))))
                .build();

        String sqlGenre = "SELECT *\n" +
                "FROM GENRE g \n" +
                "WHERE g.GENRE_ID IN (\n" +
                "\tSELECT gf.GENRE_ID\n" +
                "\tFROM GENRE_FILM AS gf \n" +
                "\tWHERE gf.Film_id = ?)";
        List<Genre> genres = jdbcTemplate.query(sqlGenre,
                (rs, rowNum) -> new Genre(rs.getInt("GENRE_ID"), rs.getString("NAME")),
                film.getId());
        film.setGenres(new HashSet<>(genres));

        String sqlLikes = "SELECT USER_ID \n" +
                "\tFROM LIKES l \n" +
                "\tWHERE FILM_ID  = ?";
        Set<Integer> likes = new HashSet<>(jdbcTemplate.query(sqlLikes,
                (rs, rowNum) -> rs.getInt("USER_ID"), film.getId()));
        film.setLikes(likes);

        return film;
    }

    private int getMaxIdFilm() {
        String sqlgetId = "SELECT FILM_ID \n" +
                "FROM FILMS u \n" +
                "ORDER BY FILM_ID DESC \n" +
                "LIMIT 1";

        List<Integer> id = jdbcTemplate.query(sqlgetId, (rs, rowNum) -> rs.getInt("FILM_ID"));
        return id.get(0);
    }

    private void disconnectFilmGenre(Film film) {
        jdbcTemplate.update("DELETE FROM GENRE_FILM WHERE FILM_ID = ?", film.getId());
        log.trace("Связь жанров в фильме id №{} разорвана", film.getId());
    }

    private void connectFilmGenre(Film film) {
        jdbcTemplate.batchUpdate("INSERT INTO GENRE_FILM (FILM_ID, GENRE_ID) VALUES ( ?,? )",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Genre genre = new ArrayList<>(film.getGenres()).get(i);
                        ps.setInt(1,film.getId());
                        ps.setInt(2,genre.getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return film.getGenres().size();
                    }
                });
        log.trace("Связь жанров в фильме id №{} установлена", film.getId());
    }

    private boolean isValidate(Film film) {
        if (film.getReleaseDate().isBefore(BEGIN_FILMS)) {
            throw new ValidationException("Дата релиза некорректно указана (id Фильма № " + film.getId() + " )");
        }
        return true;
    }

    public void addLike(Integer idFilm, Integer idUser) {
        jdbcTemplate.update("INSERT INTO LIKES (FILM_ID,USER_ID) VALUES (?,?)", idFilm, idUser);
    }

    public void removeLike(Integer idFilm, Integer idUser) {
        jdbcTemplate.update("DELETE FROM LIKES WHERE FILM_ID =? AND USER_ID =?", idFilm, idUser);
    }

    public List<Film> bestFilms(Integer count) {
        String sql = " SELECT f.*\n" +
                "        FROM FILMS f\n" +
                "        LEFT OUTER JOIN likes AS l ON f.FILM_ID = l.FILM_ID\n" +
                "        GROUP BY f.FILM_ID\n" +
                "        ORDER BY COUNT(l.USER_ID) DESC,\n" +
                "        f.FILM_ID " +
                "        LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }

}