package ru.yandex.practicum.filmorate.model;

import java.util.HashMap;
import java.util.Map;

public enum Genre {
    COMEDY,
    DRAMA,
    CARTOON,
    THRILLER,
    DOCUMENTARY,
    ACTION;

    public static Integer exportGenre(Genre genre) {
        switch (genre) {
            case COMEDY:
                return 1;
            case DRAMA:
                return 2;
            case CARTOON:
                return 3;
            case THRILLER:
                return 4;
            case DOCUMENTARY:
                return 5;
            case ACTION:
                return 6;
            default:
                throw new NullPointerException("Ошибка экспорта жанра фильма");
        }
    }
    public static Genre importGenre(Integer id) {
        switch (id) {
            case 1:
                return COMEDY;
            case 2:
                return DRAMA;
            case 3:
                return CARTOON;
            case 4:
                return THRILLER;
            case 5:
                return DOCUMENTARY;
            case 6:
                return ACTION;
            default:
                throw new NullPointerException("Ошибка импорта жанра фильма");
        }
    }
    public static Map<Integer,Genre> get(Integer id) {

            Map<Integer,Genre> map = new HashMap<>();
            map.put(id,Genre.importGenre(id));
            return map;
    }

    public static Map<Integer,Genre> getAll() {
        Map<Integer,Genre> map = new HashMap<>();
        Genre[] genres = Genre.values();
        int i=1;
        for (Genre genre: genres) {
            map.put(i++,genre);
        }
        return map;
    }
}