package ru.yandex.practicum.filmorate.controller;

import java.util.List;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    @Qualifier ("dbFilmStorage")
    private final FilmStorage filmStorage;

    @Qualifier("dbFilmService")
    private final FilmService filmService;

    @GetMapping
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        return filmStorage.add(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmStorage.update(film);
    }

    @GetMapping ("/{id}")
    public Film get(@PathVariable Integer id) {
        return filmStorage.get(id);
    }

    @PutMapping ("/{id}/like/{userId}")
    public List<Integer> putLikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmService.addLike(id,userId);
    }

    @DeleteMapping ("/{id}/like/{userId}")
    public List<Integer> removeLikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmService.removeLike(id,userId);
    }

    @GetMapping ("/popular")
    public List<Film> bestFilms(@RequestParam(value = "count", defaultValue = "10") Integer count) {
        return filmService.bestFilms(count);
    }
}
