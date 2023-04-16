package ru.yandex.practicum.filmorate.model;

import java.util.Arrays;
import java.util.List;

public enum Rating {
    G,
    PG,
    PG13,
    R,
    NC17;

    public static String exportRating(Rating rating) {
        switch (rating) {
            case G: return "G";
            case PG: return "PG";
            case PG13: return "PG-13";
            case R: return "R";
            case NC17: return "NC-17";
            default: throw new NullPointerException("Ошибка экспорта рейтинга фильма");
        }
    }

    public static Rating importRating(String rating) {
        switch (rating) {
            case "G": return G;
            case "PG": return PG;
            case "PG-13": return PG13;
            case "R": return R;
            case "NC-17": return NC17;
            default: throw new NullPointerException("Ошибка импорта рейтинга фильма");
        }
    }
    public static List<Rating> getAll() {
        return Arrays.asList(Rating.values());
    }
}
