package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.dto.OnlineShopExceptionResponse;
import net.thumbtack.onlineshop.dto.ValidationErrorResponse;
import net.thumbtack.onlineshop.exeption.OnlineShopErrorCode;
import net.thumbtack.onlineshop.exeption.OnlineShopException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    OnlineShopExceptionResponse handleInternalError(HttpServletRequest req, Throwable ex) {
        return new OnlineShopExceptionResponse(new OnlineShopException("", OnlineShopErrorCode.INTERNAL_SERVER_ERROR));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CannotCreateTransactionException.class)
    @ResponseBody
    OnlineShopExceptionResponse handleDatabaseCommunicationError(HttpServletRequest req, CannotCreateTransactionException ex) {
        return new OnlineShopExceptionResponse(new OnlineShopException("", OnlineShopErrorCode.DATABASE_UNAVAILABLE));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseBody
    OnlineShopExceptionResponse handleDuplicateKeyError(HttpServletRequest req, DuplicateKeyException ex) {
        String[] split = ex.getCause().getMessage().split("'");
        return new OnlineShopExceptionResponse(new OnlineShopException(split[1], OnlineShopErrorCode.DATABASE_DUPLICATE_KEY));
    }

    @ExceptionHandler(OnlineShopException.class)
    ResponseEntity<OnlineShopExceptionResponse> handleOnlineShopException(OnlineShopException exc) {
        return ResponseEntity.badRequest().body(new OnlineShopExceptionResponse(exc));
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity.badRequest().body(new OnlineShopExceptionResponse(new OnlineShopException("", OnlineShopErrorCode.WRONG_PARAM)));
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity.badRequest().body(new OnlineShopExceptionResponse(new OnlineShopException("", OnlineShopErrorCode.WRONG_REQUEST)));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity.badRequest().body(new OnlineShopExceptionResponse(new OnlineShopException("", OnlineShopErrorCode.REQUEST_NOT_READABLE)));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            error.getResponses().add(
                    new OnlineShopExceptionResponse(OnlineShopErrorCode.VALIDATION_ERROR, fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return ResponseEntity.badRequest().body(error);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity.badRequest().body(new OnlineShopExceptionResponse(new OnlineShopException("", OnlineShopErrorCode.WRONG_URL)));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity.badRequest().body(new OnlineShopExceptionResponse(new OnlineShopException("", OnlineShopErrorCode.EMPTY_REQUEST)));
    }

}