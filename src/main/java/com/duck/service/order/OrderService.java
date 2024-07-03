package com.duck.service.order;

import com.duck.dto.order.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.duck.entity.Order;

import java.security.Principal;
import java.util.List;

public interface OrderService {
    void saveChangedSkusQuantity(final Order order);

    void trackingOrder(final OrderStatusPayload payload);

    Order findByOrderCode(final String orderCode);

    Order findByVnpTxnRef(final String vnp_TxnRef);

    OrderResponse createV2(final OrderDTO order, final Principal principal);

    void updateOrderCode(final Long orderId, final String orderCode);

    OrderResponse createOrder(final OrderDTO order, final Principal principal) throws JsonProcessingException;

    OrderResponse create(final OrderDTO order, final Principal principal);

    OrderResponse createFromCart(final OrderDTO order, final Principal principal);

    OrderResponse createWithVNPay(final OrderDTO order, final String username, final String vnp_TxnRef);

    OrderResponse createWithVNPayV2(final Long orderId, final String vnp_TxnRef);

    OrderDetailResponse getById(final Long orderId);

    Order findById(final Long orderId);

    Order findByItemId(final Long itemId);

    String isDeletedById(final Long orderId);

    String deleteById(final Long orderId);

    OrderResponse updateStatus(final Long orderId, final String statusValue) throws JsonProcessingException;

    List<OrderResponse> getOrdersByUsername(final String username);

    List<OrderResponse> getOrdersByUserId(final Long userId);

    List<OrderResponse> findYourOrderByStatus(final String value, final Principal principal);

    List<OrderResponse> getYourOrders(final Principal principal);

    CheckOutDTO getDataFromUserInfor(final Principal principal);

    void paymentCompleted(final String vnp_TxnRef) throws JsonProcessingException;

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

    OrderResponse makePaymentForCOD(final OrderDTO order, final Long orderId) throws JsonProcessingException;

    void makePaymentForVNPAY(final OrderDTO dto);
}
