package com.orders.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orders.constant.Messages;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@SpringBootTest
@AutoConfigureMockMvc
public abstract class ControllerTemplateTest<T> {

	@Autowired
	protected MockMvc mockMvc;

	protected MvcResult response;

	private ObjectMapper jsonMapper;

	private T controller;

	private ReloadableResourceBundleMessageSource messageSource;

	@BeforeEach
	public void setup() {
		jsonMapper = new ObjectMapper();
		messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:messages");
		messageSource.setDefaultEncoding("UTF-8");
	}

	protected String toJsonString(final Object obj) {
		try {
			return jsonMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
	}

	/*
	 * Then methods
	 */
	protected void thenExpectOKStatus() {
		assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());
	}

	protected void thenExpectNotFoundStatus() {
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getResponse().getStatus());
	}

	protected void thenExpectNoContentStatus() {
		assertEquals(HttpStatus.NO_CONTENT.value(), response.getResponse().getStatus());
	}

	protected void thenExpectCreatedStatus() {
		assertEquals(HttpStatus.CREATED.value(), response.getResponse().getStatus());
	}

	protected void thenExpectBadRequestStatus() {
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());
	}

	protected void thenExpectUnprocessableEntityStatus() {
		assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getResponse().getStatus());
	}

	protected void thenExpectedRequiredFieldError(String field) throws UnsupportedEncodingException {
		var errorMessage = getMessage(Messages.REQUIRED_FIELD, field);
		assertTrue(response.getResponse().getContentAsString().contains(errorMessage));
	}

	protected void thenExpectedInvalidFieldFormatError(String field) throws UnsupportedEncodingException {
		var errorMessage = getMessage(Messages.INVALID_FIELD_FORMAT, field);
		assertTrue(response.getResponse().getContentAsString().contains(errorMessage));
	}

	protected void thenExpectedInvalidFieldSizeError(String field, Integer size) throws UnsupportedEncodingException {
		var errorMessage = getMessage(Messages.INVALID_FIELD_SIZE, field, size);
		assertTrue(response.getResponse().getContentAsString().contains(errorMessage));
	}

	protected void thenExpectedInvalidDateRangeError() throws UnsupportedEncodingException {
		var errorMessage = getMessage(Messages.INVALID_DATE_RANGE);
		assertTrue(response.getResponse().getContentAsString().contains(errorMessage));
	}

	protected void thenExpectedInvalidFieldEmptyError(String field) throws UnsupportedEncodingException {
		var errorMessage = getMessage(Messages.INVALID_FIELD_EMPTY, field);
		assertTrue(response.getResponse().getContentAsString().contains(errorMessage));
	}
}
