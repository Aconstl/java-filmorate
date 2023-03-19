package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.customException.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FIlmControllerTest {

    private Validator validator;
    Film film;
    @BeforeEach
    public void beforeEach() {
       ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        film = Film.builder()
                .name("Кино")
                .description("описание кино")
                .releaseDate(LocalDate.of(1895,12,28))
                .duration(20)
                .build();
    }

    @Test
    public void nameFilmTest() throws ValidationException {
        Set<ConstraintViolation<Film>> violation = validator.validate(film);
        assertTrue(violation.isEmpty());

        film.setName("");
        Set<ConstraintViolation<Film>> violationFalse = validator.validate(film);
        assertFalse(violationFalse.isEmpty());
    }


    @Test
    public void descriptionFilmTest() throws ValidationException {
        Set<ConstraintViolation<Film>> violation = validator.validate(film);
        assertTrue(violation.isEmpty());

        film.setDescription("1".repeat(201));
        Set<ConstraintViolation<Film>> violationFalse = validator.validate(film);
        assertFalse(violationFalse.isEmpty());

        film.setDescription("1".repeat(200));
        Set<ConstraintViolation<Film>> violationTrue = validator.validate(film);
        assertTrue(violationTrue.isEmpty());
    }

    @Test
    public void dataReliseFilmTest() throws ValidationException {

        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmService(filmStorage,userStorage);
        FilmController filmController = new FilmController(filmStorage,filmService);

        Film filmAddTrue = filmController.add(film);
        assertEquals(filmAddTrue, film);

        film.setReleaseDate(LocalDate.of(1895,12,22));
        ValidationException ve1 = assertThrows(ValidationException.class,
                () -> filmController.add(film));
        assertEquals(ve1.getMessage(),"Дата релиза некорректно указана (id Фильма № " + film.getId() + " )");

        film.setReleaseDate(LocalDate.now());
        Film trueFilm = filmController.add(film);
        assertEquals(trueFilm, film);
    }

    @Test
    public void durationFilmTest() throws ValidationException {
        Set<ConstraintViolation<Film>> violation = validator.validate(film);
        assertTrue(violation.isEmpty());

        film.setDuration(0);
        Set<ConstraintViolation<Film>> violationFalse = validator.validate(film);
        assertFalse(violationFalse.isEmpty());

        film.setDuration(1);
        Set<ConstraintViolation<Film>> violationTrue = validator.validate(film);
        assertTrue(violationTrue.isEmpty());
    }
}