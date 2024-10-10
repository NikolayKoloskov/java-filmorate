package ru.yandex.practicum.filmorate.exceptions;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ConstraintViolationException e) {
        log.error("Ошибка валидации: %s", e.getMessage());
        return new ErrorResponse(String.format("Ошибка валидации: %s", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        e.getBindingResult().getAllErrors().forEach(error -> log.error(error.getDefaultMessage()));
        return new ErrorResponse(String.format("Ошибка валидации JSON, параметр: %s",
                e.getBindingResult().getAllErrors().getFirst().getDefaultMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.error("Не найдено: %s", e.getMessage());
        return new ErrorResponse(String.format("Не найдено: %s", e.getMessage()));
    }

    @ExceptionHandler(NotFoundUserException.class)
    @ResponseStatus(HttpStatus.OK)
    public ErrorResponse handleNotFoundException(NotFoundUserException e) {
        log.error("Не найдено: %s", e.getMessage());
        return new ErrorResponse(String.format("Не найдено: %s", e.getMessage()));
    }

    @ExceptionHandler(DuplicatedDataException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleDuplicatedDataException(DuplicatedDataException e) {
        log.error("Обнаружено дублирующееся значение: %s", e.getMessage());
        return new ErrorResponse(String.format("Обнаружено дублирующееся значение: %s", e.getMessage()));
    }

    @ExceptionHandler(ConditionsNotMetException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConditionsNotMetException(ConditionsNotMetException e) {
        return new ErrorResponse(String.format("Условия не выполнены: %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Throwable e) {
        return new ErrorResponse(String.format("Ошибка сервера: %s", e.getMessage()));
    }

    @ExceptionHandler(DataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(Exception e) {
        return new ErrorResponse(String.format("Ошибка сервера: %s", e.getMessage()));
    }
}
