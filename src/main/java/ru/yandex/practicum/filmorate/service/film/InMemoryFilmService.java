package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Qualifier("memoryFilmService")
public class InMemoryFilmService implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmService(@Qualifier("memoryFilmStorage") FilmStorage filmStorage, @Qualifier("memoryUserStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public List<Integer> addLike(Integer idFilm, Integer idUser) {
        log.trace("Добавление лайка");
        Film film = filmStorage.get(idFilm);
        if (isValid(idUser)) {
            film.getLikes().add(idUser);
            log.debug("Пользователь с id №{} поставил лайк фильму \"{}\" (id№ {})", idUser, film.getName(), idFilm);
        }
        return new ArrayList<>(film.getLikes());
    }

    @Override
    public List<Integer> removeLike(Integer idFilm, Integer idUser) {
        log.trace("Удаление лайка");
        Film film = filmStorage.get(idFilm);
        if (isValid(idUser)) {
            film.getLikes().remove(idUser);
            log.debug("Пользователь с id №{} убрал лайк фильму \"{}\" (id№ {})", idUser, film.getName(), idFilm);
        }
        return new ArrayList<>(film.getLikes());
    }

    @Override
    public List<Film> bestFilms(Integer count) {
        log.trace("Вывод лучших фильмов");
        return filmStorage.getAll().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    private boolean isValid(Integer idUser) {
        userStorage.get(idUser);
        return true;
    }
}
