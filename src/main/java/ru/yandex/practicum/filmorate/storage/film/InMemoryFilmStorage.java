package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customException.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final LocalDate BEGIN_FILMS = LocalDate.of(1895,12,28);
    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private final Map<Integer,Film> films = new HashMap<>();
    private int idFilm = 1;

    @Override
    public List<Film> getAll() {
        log.trace("Вывод списка всех фильмов");
        List<Film> filmsList = new ArrayList<>();
        films.forEach((key, value) -> filmsList.add(value));
        return filmsList;
    }

    @Override
    public Film add(Film film){
        log.trace("Добавление нового фильма");
        if (isValidate(film)) {
            if (film.getLikes() == null) {
                film.setLikes(new HashSet<>());
            }
            film.setId(idFilm++);
            films.put(film.getId(), film);
            log.debug("Фильм \"{}\" (id №{}) добавлен", film.getName(),film.getId());
        }
        return film;
    }
    @Override
    public Film update(Film film ) {
        log.trace("Обновление фильма");
        if (film.getId() ==0 || !films.containsKey(film.getId())) {
            log.error("Фильм \"{}\" (id №{}) не найден", film.getName(),film.getId());
            throw new IllegalArgumentException("Фильм для обновления не найден");
        } else if (isValidate(film)) {
            if (film.getLikes() == null) {
                film.setLikes(new HashSet<>());
            }
            films.put(film.getId(), film);
            log.debug("Фильм \"{}\" (id №{}) обновлен", film.getName(),film.getId());
        }
        return film;
    }

    @Override
    public Film get (Integer id) {
        log.trace("Получение фильма");
        if (id == null) {
            log.error("ID фильма указан неверно");
            throw new NullPointerException("ID фильма указан неверно");
        } else if (!films.containsKey(id)) {
            log.error("Фильм не найден");
            throw new IllegalArgumentException("Фильм не найден");
        }
        log.debug("Фильм \"{}\" (id №{}) получен", films.get(id).getName(),id);
        return films.get(id);
    }
    private boolean isValidate(Film film) {
        if (film.getReleaseDate().isBefore(BEGIN_FILMS)) {
            log.error("Ошибка валидации id № {} : дата релиза некорректная", film.getId());
            throw new ValidationException("Дата релиза некорректно указана");
        }
        return true;
    }
}