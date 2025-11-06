package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFoundException e) {
        log.debug("Ошибка, объект не найден. {}", e.getMessage());
        return new ErrorResponse(
                "Ошибка, объект не найден",
                e.getMessage()
        );
    }

    @ExceptionHandler({ItemUnavailableException.class, UserNotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleExceptions(Exception e) {
        if (e.getClass() == ItemUnavailableException.class) {
            return new ErrorResponse("Некорректное значение", "Вещь недоступна для бронирования");
        } else if (e.getClass() == UserNotFoundException.class) {
            return new ErrorResponse(
                    "Пользователь не найден",
                    e.getMessage()
            );
        }
        return new ErrorResponse("Ошибка", "Произошла неизвестная ошибка");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicate(final DuplicatedDataException e) {
        return new ErrorResponse(
                "Ошибка, объект с такими данными уже существует",
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServerError(final Exception e) {
        return new ErrorResponse(
                "Ошибка",
                e.getMessage()
        );
    }
}
