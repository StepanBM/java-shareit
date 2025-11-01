package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFoundException e) {
        //log.debug("Ошибка, объект не найден. {}", e.getMessage());
        return new ErrorResponse(
                "Ошибка, объект не найден",
                e.getMessage()
        );
    }

    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class, ConstraintViolationException.class,
            HttpMessageNotReadableException.class, MissingRequestHeaderException.class, ItemUnavailableException.class, UserNotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final Exception e) {
       // log.debug("Ошибка валидации. {}", e.getMessage());
        if (e.getClass() == ValidationException.class) {
            return new ErrorResponse(
                    "Некорректное значение параметра " , e.getMessage()
            );
        } else if (e.getClass() == HttpMessageNotReadableException.class) {
            return new ErrorResponse("Некорректный запрос", "Тело запроса отсутствует");
        } else if (e.getClass() == MissingRequestHeaderException.class) {
            return new ErrorResponse("Некорректный запрос", "Заголовок отсутствует");
        } else if (e.getClass() == ConstraintViolationException.class) {
            return new ErrorResponse("Некорректное значение", "Ошибка валидации");
        } else if (e.getClass() == ItemUnavailableException.class) {
            return new ErrorResponse("Некорректное значение", "Вещь недоступна для бронирования");
        } else if (e.getClass() == UserNotFoundException.class) {
            return new ErrorResponse("Некорректное значение", "Пользователя нет");
        } else {
            return new ErrorResponse(
                    "Некорректное значение параметра " + ((MethodArgumentNotValidException) e).getParameter(),
                    e.getMessage()
            );
        }
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
