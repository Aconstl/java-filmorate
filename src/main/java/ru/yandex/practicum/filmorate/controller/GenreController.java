package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Map;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    @GetMapping
    public Map<Integer,Genre> getAll() {
        return Genre.getAll();
    }

    @GetMapping ("/{id}")
    public Map<Integer,Genre> get(@PathVariable Integer id){
        return Genre.get(id);
    }

}
