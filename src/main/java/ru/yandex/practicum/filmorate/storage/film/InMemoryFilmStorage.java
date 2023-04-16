package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customException.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Qualifier("memoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer,Film> films = new HashMap<>();
    private int idFilm = 1;

    @Override
    public List<Film> getAll() {
        log.trace("Вывод списка всех фильмов");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film add(Film film){
        log.trace("Добавление нового фильма");
        if (isValidate(film)) {
            if (film.getLikes() == null) {
                film.setLikes(new HashSet<>());
            }
            film.setId(idFilm++);
            films.put(film.getId(), film);
            log.debug("Фильм \"{}\" (id №{}) добавлен", film.getName(),film.getId());
        }
        return film;
    }
    @Override
    public Film update(Film film ) {
        log.trace("Обновление фильма");
        if (film.getId() ==0 || !films.containsKey(film.getId())) {
            throw new IllegalArgumentException("Фильм c id № " + film.getId() + " не найден");
        } else if (isValidate(film)) {
            Set<Integer> likesOldVersionFilm = films.get(film.getId()).getLikes();
            film.setLikes(new HashSet<>(likesOldVersionFilm));
            films.put(film.getId(), film);
            log.debug("Фильм \"{}\" (id №{}) обновлен", film.getName(),film.getId());
        }
        return film;
    }

    @Override
    public Film get (Integer id) {
        log.trace("Получение фильма");
        if (id == null) {
            throw new NullPointerException("ID фильма указан неверно");
        } else if (!films.containsKey(id)) {
            throw new IllegalArgumentException("Фильм c id № " + id + " не найден");
        }
        log.debug("Фильм \"{}\" (id №{}) получен", films.get(id).getName(),id);
        return films.get(id);
    }





    private boolean isValidate(Film film) {
        if (film.getReleaseDate().isBefore(BEGIN_FILMS)) {
            throw new ValidationException("Дата релиза некорректно указана (id Фильма № "+ film.getId() + " )");
        }
        return true;
    }
}
