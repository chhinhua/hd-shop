package com.hdshop.service.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hdshop.dto.order.CheckOutDTO;
import com.hdshop.dto.order.OrderDTO;
import com.hdshop.dto.order.OrderPageResponse;
import com.hdshop.dto.order.OrderResponse;
import com.hdshop.entity.Order;

import java.security.Principal;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(final OrderDTO order, final Principal principal) throws JsonProcessingException;

    OrderResponse create(final OrderDTO order, final Principal principal);

    OrderResponse createFromCart(final OrderDTO order, final Principal principal);

    OrderResponse createWithVNPay(final OrderDTO order, final String username, final String vnp_TxnRef);

    OrderResponse getById(final Long orderId);

    Order findById(final Long orderId);

    Order findByItemId(final Long itemId);

    String isDeletedById(final Long orderId);

    String deleteById(final Long orderId);

    OrderResponse updateStatus(final Long orderId, final String statusValue);

    List<OrderResponse> getOrdersByUsername(final String username);

    List<OrderResponse> getOrdersByUserId(final Long userId);

    List<OrderResponse> findYourOrderByStatus(final String value, final Principal principal);

    List<OrderResponse> getYourOrders(final Principal principal);

    CheckOutDTO getDataFromUserInfor(final Principal principal);

    void paymentCompleted(final String vnp_TxnRef);


    OrderPageResponse adminFilter(
            String statusValue,
            String key,
            List<String> sortCriteria,
            int pageNo,
            int pageSize
    );

    OrderPageResponse userFilter(
            String statusValue,
            String key,
            int pageNo,
            int pageSize,
            final Principal principal
    );

    OrderResponse makePaymentForCOD(final OrderDTO order, final Long orderId);

    void makePaymentForVNPAY(final OrderDTO dto, final Long orderId);
}
