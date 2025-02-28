package com.orders.controller;

import com.orders.model.Order;
import com.orders.model.User;
import com.orders.service.UserOrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

public class UserOrderControllerTest extends ControllerTemplateTest<UserOrderController> {

    private String orderId;
    private String startOrderDate;
    private String endOrderDate;

    @MockitoBean
    private UserOrderService userOrderService;

    @Test
    @DisplayName("Try to get user orders with success. Return HTTP 200")
    void shouldFindUserOrdersSuccess() throws Exception {
        givenFindByUserIdAndOrderDateReturnUsers();
        whenCallGetUserOrders();
        thenExpectOKStatus();
    }

    @Test
    @DisplayName("Try to get user orders with String orderId. Return HTTP 400")
    void shouldNotFindUserOrdersWhenOrderIdInvalid() throws Exception {
        givenInvalidOrderId();
        whenCallGetUserOrders();
        thenExpectBadRequestStatus();
        thenExpectedInvalidFieldFormatError("orderId");
    }

    @Test
    @DisplayName("Try to get user orders with wrong startOrderDate. Return HTTP 400")
    void shouldNotFindUserOrdersWhenStartOrderDateInvalid() throws Exception {
        givenInvalidStartOrderDate();
        whenCallGetUserOrders();
        thenExpectBadRequestStatus();
        thenExpectedInvalidFieldFormatError("startOrderDate");
    }

    @Test
    @DisplayName("Try to get user orders with wrong endOrderDate. Return HTTP 400")
    void shouldNotFindUserOrdersWhenEndOrderDateInvalid() throws Exception {
        givenInvalidEndOrderDate();
        whenCallGetUserOrders();
        thenExpectBadRequestStatus();
        thenExpectedInvalidFieldFormatError("endOrderDate");
    }

    @Test
    @DisplayName("Try to get user orders with End date after Start date filter. Return HTTP 400")
    void shouldNotFindUserOrdersWhenEndDateAfterBeginDate() throws Exception {
        givenEndDateAfterStartDate();
        whenCallGetUserOrders();
        thenExpectBadRequestStatus();
        thenExpectedInvalidDateRangeError();
    }

    @Test
    @DisplayName("Try to get user orders just with start date filter. Return HTTP 400")
    void shouldNotFindUserOrdersWhenOnlyStartDate() throws Exception {
        givenStartDateOnly();
        whenCallGetUserOrders();
        thenExpectBadRequestStatus();
        thenExpectedInvalidDateRangeError();
    }

    @Test
    @DisplayName("Try to get user orders just with end date filter. Return HTTP 400")
    void shouldNotFindUserOrdersWhenOnlyEndDate() throws Exception {
        givenEndDateOnly();
        whenCallGetUserOrders();
        thenExpectBadRequestStatus();
        thenExpectedInvalidDateRangeError();
    }

	@Test
	@DisplayName("Try to post new users without file. Return HTTP 400")
	void shouldNotUploadUserOrdersWithEmptyFile() throws Exception {
		whenCallUploadUserOrders();
		thenExpectBadRequestStatus();
        thenExpectedRequiredFieldError("file");
	}
    /*
     *	Given methods
     */
    private void givenFindByUserIdAndOrderDateReturnUsers() {
        doReturn(List.of(User.builder().userId(1L)
                .name("John Wayne")
                .orders(List.of(
                        Order.builder().build()
                ))
                .build()))
                .when(userOrderService)
                .findByUserIdAndOrderDate(anyLong(), any(LocalDate.class), any(LocalDate.class));
    }

    private void givenInvalidOrderId() {
        orderId = "122@";
    }

    private void givenInvalidStartOrderDate() {
        startOrderDate = "dsada";
    }

    private void givenInvalidEndOrderDate() {
        endOrderDate = "dsada";
    }

    private void givenEndDateAfterStartDate() {
        startOrderDate = "2021-03-19";
        endOrderDate = "2021-03-05";
    }

    private void givenStartDateOnly() {
        startOrderDate = "2021-03-19";
    }

    private void givenEndDateOnly() {
        endOrderDate = "2021-03-05";
    }

    /*
     * When methods
     */
    private void whenCallGetUserOrders() throws Exception {
        response = mockMvc.perform(get("/users/orders")
                .param("orderId", orderId)
                .param("startOrderDate", startOrderDate)
                .param("endOrderDate", endOrderDate)
                .contentType(MediaType.APPLICATION_JSON)).andReturn();
    }

    private void whenCallUploadUserOrders() throws Exception {
        response = mockMvc.perform(multipart("/users/orders")
                        .param("file", ""))
                .andReturn();
    }

    /*
     *	Then methods
     */

}
