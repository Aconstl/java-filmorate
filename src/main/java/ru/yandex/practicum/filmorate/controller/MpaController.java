package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.mpa.DbMpa;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    @Qualifier("dbMpa")
    private final DbMpa dbMpa;

    @GetMapping
    public List<Mpa> getAll() {
        return dbMpa.getAll();
    }

    @GetMapping ("/{id}")
    public Mpa get(@PathVariable Integer id){
        return dbMpa.get(id);
    }

}
