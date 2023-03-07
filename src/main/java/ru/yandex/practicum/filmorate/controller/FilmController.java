package ru.yandex.practicum.filmorate.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.customException.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private static final int MAX_LENGTH_DESCRIPTION = 200;
    private static final LocalDate BEGIN_FILMS = LocalDate.of(1895,12,28);

    private final Map<Integer,Film> films = new HashMap<>();
    private int idFilm = 1;

    @GetMapping
    public List<Film> getAllFilms(){
        log.trace("Вывод списка всех фильмов");
        List<Film> filmsList = new ArrayList<>();
        films.forEach((key, value) -> filmsList.add(value));
        return filmsList;
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws ValidationException {
        log.trace("Добавление нового фильма");
        if (isValidate(film)) {
            film.setId(idFilm++);
            films.put(film.getId(), film);
            log.debug("Фильм \"{}\" (id №{}) добавлен", film.getName(),film.getId());
        }
            return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film ) throws ValidationException {
        log.trace("Обновление фильма");
        if (film.getId() ==0 || !films.containsKey(film.getId())) {
            log.error("Фильм \"{}\" (id №{}) не найден", film.getName(),film.getId());
            throw new ValidationException("Фильм для обновления не найден");
        } else if (isValidate(film)) {
            films.put(film.getId(), film);
            log.debug("Фильм \"{}\" (id №{}) обновлен", film.getName(),film.getId());
        }
            return film;
    }

    private boolean isValidate(Film film) throws ValidationException {
            if (film.getName().isBlank()) {
                log.error("Ошибка валидации id № {} : неккоректное имя", film.getId());
                throw new ValidationException("Имя отсутствует");
            } else if (film.getDescription().length() > MAX_LENGTH_DESCRIPTION) {
                log.error("Ошибка валидации id № {} : имя слишком длинное", film.getId());
                throw new ValidationException("Описание превышает допустимых размеров (не более " + MAX_LENGTH_DESCRIPTION
                        + " символов)");
            } else if (film.getReleaseDate().isBefore(BEGIN_FILMS)) {
                log.error("Ошибка валидации id № {} : дата релиза некорректная", film.getId());
                throw new ValidationException("Дата релиза некорректно указана");
            } else if (film.getDuration() <= 0) {
                log.error("Ошибка валидации id № {} : продолжительность фильма равна или меньше 0", film.getId());
                throw new ValidationException("Продолжительность фильма равна или меньше 0");
            }
        return true;
    }

}
