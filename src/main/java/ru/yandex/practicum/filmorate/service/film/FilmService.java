package ru.yandex.practicum.filmorate.service.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Logger log = LoggerFactory.getLogger(FilmService.class);

    List<Integer> addLike(Integer idFilm, Integer idUser);

    List<Integer> removeLike(Integer idFilm, Integer idUser);

    List<Film> bestFilms(Integer count);


}
