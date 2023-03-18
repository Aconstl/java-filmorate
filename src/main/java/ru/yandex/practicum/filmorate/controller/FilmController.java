package ru.yandex.practicum.filmorate.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;
    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService){
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        return filmStorage.add(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film ) {
        return filmStorage.update(film);
    }

    @GetMapping ("/{id}")
    public Film get(@PathVariable Integer id){
        return filmStorage.get(id);
    }

    @PutMapping ("/{id}/like/{userId}")
    public Map<String,String> putLikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        return Map.of("Фильм id№ " + id + ": ", filmService.addLike(id,userId));
    }

    @DeleteMapping ("/{id}/like/{userId}")
    public Map<String,String> removeLikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        return Map.of("Фильм id№ " + id + ": ", filmService.removeLike(id,userId));
    }

    @GetMapping ("/popular")
    public List<Film> bestFilms(@RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        List<Film> listFilm = filmService.bestFilms(count);
        return listFilm;
    }

}
