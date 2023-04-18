package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.customException.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FIlmControllerTest {

    private final FilmController filmController;
    private Validator validator;
    Film film;

    @BeforeEach
    public void beforeEach() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        film = Film.builder()
                .name("Кино")
                .description("описание кино")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(20)
                .mpa(new Mpa(1,"G"))
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



        Film filmAddTrue = filmController.add(film);
        assertEquals(filmAddTrue, film);

        film.setReleaseDate(LocalDate.of(1895, 12, 22));
        ValidationException ve1 = assertThrows(ValidationException.class,
                () -> filmController.add(film));
        assertEquals(ve1.getMessage(), "Дата релиза некорректно указана (id Фильма № " + film.getId() + " )");

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

    @Test
    public void testFilm() {
        Film film = filmController.add(Film.builder()
                .name("Кино1")
                .description("описание кино")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(20)
                .mpa(new Mpa(1, "G"))
                .build());

        assertThat(film.toString(), containsString("Кино1"));

        Film film1 = filmController.get(film.getId());
        assertThat(film1.toString(), containsString("Кино1"));

        List<Film> films = filmController.getAll();
        assertThat(films.toString(), containsString("Кино1"));


        Film filmUPD = filmController.update(Film.builder()
                .id(1)
                .name("КиноОбновленное")
                .description("описание кино")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(20)
                .mpa(new Mpa(1, "G"))
                .build());

        assertThat(filmUPD.toString(), containsString("КиноОбновленное"));
    }
}