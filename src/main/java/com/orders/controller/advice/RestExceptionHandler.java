package com.orders.controller.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.orders.constant.Messages;
import com.orders.exception.NotFoundException;
import com.orders.exception.UnprocessableEntityException;
import com.orders.response.MessageError;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class RestExceptionHandler {

	private final MessageSource messageSource;

	private static final String JVM_MAX_STRING_LEN = "2147483647";

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.initDirectFieldAccess();
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<List<MessageError>> handleMethodArgumentNotValidException(
			BindException methodArgumentNotValidException) {
		List<MessageError> messageErrors = Optional.ofNullable(methodArgumentNotValidException)
                .filter(argumentNotValidException -> !ObjectUtils.isEmpty(argumentNotValidException.getBindingResult()))
                .map(BindException::getBindingResult)
                .filter(bindingResult -> !ObjectUtils.isEmpty(bindingResult.getAllErrors()))
                .map(BindingResult::getAllErrors).stream().flatMap(Collection::stream)
				.filter(objectError -> !ObjectUtils.isEmpty(objectError)).map(this::createError)
				.collect(Collectors.toList());
		return new ResponseEntity<>(messageErrors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<List<MessageError>> handleHttpMessageNotReadableException(
			HttpMessageNotReadableException httpMessageNotReadableException) {

		List<MessageError> errors = Optional.ofNullable(httpMessageNotReadableException.getCause())
                .filter(InvalidFormatException.class::isInstance).map(InvalidFormatException.class::cast)
                .map(InvalidFormatException::getPath).stream()
				.flatMap(Collection::stream).filter(invalidItem -> invalidItem.getFieldName() != null)
				.map(invalidItem -> MessageError.builder().code(Messages.INVALID_FIELD_FORMAT)
						.message(getMessage(Messages.INVALID_FIELD_FORMAT, invalidItem.getFieldName())).build())
				.collect(Collectors.toList());

		if (errors.isEmpty()) {
			errors = Collections.singletonList(MessageError.builder().code(Messages.INVALID_BODY_ERROR)
                    .message(getMessage(Messages.INVALID_BODY_ERROR)).build());
		}

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Void> handleNotFoundException(NotFoundException notFoundException) {
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(UnprocessableEntityException.class)
	public ResponseEntity<MessageError> handleUnprocessableEntityException(UnprocessableEntityException unpEx) {
		return new ResponseEntity<>(MessageError.builder().code(unpEx.getCode())
				.message(getMessage(unpEx.getCode(), unpEx.getArgs())).build(), unpEx.getStatus());
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Void> handleHttpRequestMethodNotSupportedException(
			HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException) {
		return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<Void> handleHttpMediaTypeNotSupportedException(
			HttpMediaTypeNotSupportedException httpMediaTypeNotSupportedException) {
		return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<MessageError> handleException(Exception e) {
		log.error("Generic error.", e);
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<List<MessageError>> missingServletRequestParameterException(
			final MissingServletRequestParameterException e) {
		return new ResponseEntity<>(
                Collections.singletonList(MessageError.builder().code(Messages.REQUIRED_FIELD)
                        .message(getMessage(Messages.REQUIRED_FIELD, e.getParameterName())).build()),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MissingServletRequestPartException.class)
	public ResponseEntity<MessageError> missingServletRequestParameterException(
			final MissingServletRequestPartException e) {
		return new ResponseEntity<>(
				MessageError.builder().code(Messages.REQUIRED_FIELD)
						.message(getMessage(Messages.REQUIRED_FIELD, e.getRequestPartName())).build(),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<List<MessageError>> methodArgumentTypeMismatchException(
			final MethodArgumentTypeMismatchException e) {
		return new ResponseEntity<>(
                Collections.singletonList(MessageError.builder().code(Messages.INVALID_FIELD_FORMAT)
                        .message(getMessage(Messages.INVALID_FIELD_FORMAT, e.getName())).build()),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<List<MessageError>> handleConstraintViolationException(
			final ConstraintViolationException cve) {
		List<MessageError> errors = cve.getConstraintViolations().stream()
				.map(constraint -> MessageError.builder().code(constraint.getMessageTemplate())
						.message(getMessage(constraint.getMessageTemplate(),
								((PathImpl) constraint.getPropertyPath()).getLeafNode()))
						.build())
				.collect(Collectors.toList());
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	private MessageError createError(ObjectError error) {
		String field = "";
		if (error instanceof FieldError) {
			field = ((FieldError) error).getField();
		}

		if (error.getCode().equals("Size")) {
			String min = "";
			String max = "";
			if (error.getArguments().length > 2) {
				String rawMax = ((Integer) error.getArguments()[1]).toString();
				max = rawMax.equals(JVM_MAX_STRING_LEN) ? "" : rawMax;

				String rawMin = ((Integer) error.getArguments()[2]).toString();
				min = rawMin.equals("0") ? "" : rawMin;
			}

			if (StringUtils.isNotBlank(min) && StringUtils.isNotBlank(max)) {
				return MessageError.builder().code(error.getDefaultMessage())
						.message(getMessage(error.getDefaultMessage(), field, min, max)).build();
			} else if (StringUtils.isNotBlank(min)) {
				return MessageError.builder().code(error.getDefaultMessage())
						.message(getMessage(error.getDefaultMessage(), field, min)).build();
			} else if (StringUtils.isNotBlank(max)) {
				return MessageError.builder().code(error.getDefaultMessage())
						.message(getMessage(error.getDefaultMessage(), field, max)).build();
			}
		}

		return MessageError.builder().code(error.getDefaultMessage())
				.message(getMessage(error.getDefaultMessage(), field)).build();
	}

	private String getMessage(String code, Object... args) {
		return this.messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
	}
}
