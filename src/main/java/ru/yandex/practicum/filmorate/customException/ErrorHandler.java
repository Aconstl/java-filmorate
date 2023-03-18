package ru.yandex.practicum.filmorate.customException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validateException (final ValidationException e) {
        return new ErrorResponse ("Ошибка валидации: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus (HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundException (final IllegalArgumentException e) {
        return new ErrorResponse("Ошибка ввода: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus (HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse otherException (final NullPointerException e) {
        return new ErrorResponse("Исключение: ", e.getMessage());
    }

}

