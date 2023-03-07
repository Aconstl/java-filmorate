package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.*;
import org.springframework.util.Assert;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.customException.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FIlmControllerTest {
    FilmController filmController;
    Film film;
    @BeforeEach
    public void beforeAll() {
        filmController = new FilmController();
        film = Film.builder()
                .name("Кино")
                .description("описание кино")
                .releaseDate(LocalDate.of(1895,12,28))
                .duration(20)
                .build();
    }



    @Test
    public void nameFilmTest() throws ValidationException {
        assertTrue(filmController.getAllFilms().isEmpty());
        Film filmAddTrue = filmController.addFilm(film);
        assertEquals(filmAddTrue, film);

        film.setName("");
        ValidationException ve = assertThrows(ValidationException.class,
                () -> filmController.addFilm(film));
        assertEquals(ve.getMessage(),"Имя отсутствует");

        film.setName("Обновленное имя");
        Film filmUpdTrue = filmController.updateFilm(film);
        assertEquals(filmUpdTrue, film);

        filmUpdTrue.setName("");
        ValidationException ve1 = assertThrows(ValidationException.class,
                () -> filmController.updateFilm(filmUpdTrue));
        assertEquals(ve1.getMessage(),"Имя отсутствует");
    }

    @Test
    public void descriptionFilmTest() throws ValidationException {
        Film filmAddTrue = filmController.addFilm(film);
        assertEquals(filmAddTrue, film);
        film.setDescription("");
        Film notDesrpFilm = filmController.addFilm(film);
        assertEquals(notDesrpFilm, film);

        film.setDescription("описание кино набиваю текст до 200 для проверки здесь могла быть ваша реклама однако" +
                        "никто не делает такой код но я сделал передаю всем привет тем кто немного устал но держится " +
                        "завтра буде лучше чем вче");
        ValidationException ve = assertThrows(ValidationException.class,
                () -> filmController.addFilm(film));
        assertEquals(ve.getMessage(),"Описание превышает допустимых размеров (не более 200 символов)");

        film.setDescription("описание кино набиваю текст до 200 для проверки здесь могла быть ваша реклама однако" +
                "никто не делает такой код но я сделал передаю всем привет тем кто немного устал но держится " +
                "завтра буде лучше чем вч");
        Film trueFilm = filmController.addFilm(film);
        assertEquals(trueFilm, film);
    }

    @Test
    public void dataReliseFilmTest() throws ValidationException {
        Film filmAddTrue = filmController.addFilm(film);
        assertEquals(filmAddTrue, film);

        film.setReleaseDate(LocalDate.of(1895,12,22));
        ValidationException ve1 = assertThrows(ValidationException.class,
                () -> filmController.addFilm(film));
        assertEquals(ve1.getMessage(),"Дата релиза некорректно указана");

        film.setReleaseDate(LocalDate.now());
        Film trueFilm = filmController.addFilm(film);
        assertEquals(trueFilm, film);
    }

    @Test
    public void durationFilmTest() throws ValidationException {
        Film filmAddTrue = filmController.addFilm(film);
        assertEquals(filmAddTrue, film);

        film.setDuration(-1);
        ValidationException ve1 = assertThrows(ValidationException.class,
                () -> filmController.addFilm(film));
        assertEquals(ve1.getMessage(),"Продолжительность фильма равна или меньше 0");

        film.setDuration(0);
        ValidationException ve2 = assertThrows(ValidationException.class,
                () -> filmController.addFilm(film));
        assertEquals(ve1.getMessage(),"Продолжительность фильма равна или меньше 0");
    }
}