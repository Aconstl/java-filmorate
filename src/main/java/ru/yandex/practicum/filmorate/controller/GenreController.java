package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.Genre.DbGenre;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    @Qualifier("dbGenre")
    private final DbGenre dbGenre;

    @GetMapping
    public List<Genre> getAll() {
        return dbGenre.getAll();
    }

    @GetMapping ("/{id}")
    public Genre get(@PathVariable Integer id) {
        return dbGenre.get(id);
    }

}
