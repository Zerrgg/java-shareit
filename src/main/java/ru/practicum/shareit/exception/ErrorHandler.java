package ru.practicum.shareit.exception;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Ошибка валидации", ex.getLocalizedMessage());
        return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handle(EmailConflictException e) {
        return new ErrorResponse("Адрес почты уже используется", e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(UserNotFoundException e) {
        return new ErrorResponse("Недопустимое значение id", e.getMessage());
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(ItemNotFoundException e) {
        return new ErrorResponse("Недопустимое значение id", e.getMessage());
    }

    @ExceptionHandler(OwnerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(OwnerNotFoundException e) {
        return new ErrorResponse("Не найден владелец вещи", e.getMessage());
    }

    @ExceptionHandler(DeniedAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handle(DeniedAccessException e) {
        return new ErrorResponse("Отказано в доступе", e.getMessage());
    }
}
