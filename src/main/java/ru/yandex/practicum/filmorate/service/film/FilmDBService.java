package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.ArrayList;
import java.util.List;

@Service("dbFilmService")
@RequiredArgsConstructor
public class FilmDBService implements FilmService {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @Override
    public List<Integer> addLike(Integer idFilm, Integer idUser) {
        log.trace("Добавление лайка");
        if (isValid(idUser)) {
            filmStorage.addLike(idFilm,idUser);
            Film film = filmStorage.get(idFilm);
            log.debug("Пользователь с id №{} поставил лайк фильму \"{}\" (id№ {})", idUser, film.getName(), idFilm);
            return new ArrayList<>(film.getLikes());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Integer> removeLike(Integer idFilm, Integer idUser) {
        log.trace("Удаление лайка");
        if (isValid(idUser)) {
            filmStorage.removeLike(idFilm,idUser);
            Film film = filmStorage.get(idFilm);
            log.debug("Пользователь с id №{} убрал лайк фильму \"{}\" (id№ {})", idUser, film.getName(), idFilm);
            return new ArrayList<>(film.getLikes());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Film> bestFilms(Integer count) {
        log.trace("Вывод лучших фильмов");
        return filmStorage.bestFilms(count);
    }

    private boolean isValid(Integer idUser) {
        userStorage.get(idUser);
        return true;
    }


}
