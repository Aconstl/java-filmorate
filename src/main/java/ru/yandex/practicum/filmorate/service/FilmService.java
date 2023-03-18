package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    @Autowired
    public FilmService(FilmStorage filmStorage,UserStorage userStorage){
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public String addLike(Integer idFilm, Integer idUser) {
        log.trace("Добавление лайка");
        Film film = filmStorage.get(idFilm);
        if (isValid(idUser)) {
            film.getLikes().add(idUser);
            log.debug("Пользователь с id №{} поставил лайк фильму \"{}\" (id№ {})", idUser,film.getName(),idFilm);
        }
        return "лайк добавлен";
    }

    public String removeLike(Integer idFilm, Integer idUser) {
        log.trace("Удаление лайка");
        Film film = filmStorage.get(idFilm);
        if (isValid(idUser)) {
            film.getLikes().remove(idUser);
            log.debug("Пользователь с id №{} убрал лайк фильму \"{}\" (id№ {})", idUser,film.getName(),idFilm);
        }
        return "лайк удален";
    }

    public List<Film> bestFilms(Integer count) {
        log.trace("Вывод лучших фильмов");
        List<Film> films = filmStorage.getAll();
        return films.stream().sorted((f0,f1) -> {
            Integer s0 = f0.getLikes().size();
            Integer s1 = f1.getLikes().size();
            return  s1.compareTo(s0);
            }).limit(count).collect(Collectors.toList());
    }

    private boolean isValid(Integer idUser) {
        userStorage.get(idUser);
        return true;
    }
}
