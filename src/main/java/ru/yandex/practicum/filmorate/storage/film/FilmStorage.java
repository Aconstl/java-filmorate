package ru.yandex.practicum.filmorate.storage.film;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;


public interface FilmStorage {
    LocalDate BEGIN_FILMS = LocalDate.of(1895, 12, 28);
    Logger log = LoggerFactory.getLogger(FilmStorage.class);

    List<Film> getAll();

    Film add(Film film);

    Film update(Film film);

    Film get(Integer id);

}
