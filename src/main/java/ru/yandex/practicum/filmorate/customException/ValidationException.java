package ru.yandex.practicum.filmorate.customException;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}