package ru.yandex.practicum.filmorate.customException;

public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
