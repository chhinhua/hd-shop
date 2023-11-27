package com.hdshop.service.order;

import com.hdshop.dto.order.CheckOutDTO;
import com.hdshop.dto.order.OrderDTO;
import com.hdshop.dto.order.OrderPageResponse;
import com.hdshop.dto.order.OrderResponse;
import com.hdshop.entity.Order;

import java.security.Principal;
import java.util.List;

public interface OrderService {
    OrderResponse addOrder(final OrderDTO order, final Principal principal);

    OrderResponse createOrderFromUserCart(final OrderDTO order, final Principal principal);

    OrderResponse createOrderWithVNPay(final OrderDTO order, final String username, final String vnp_TxnRef);

    OrderResponse getOrderById(final Long orderId);

    Order findById(final Long orderId);

    Order findByItemId(final Long itemId);

    String isDeletedOrderById(final Long orderId);

    String deleteOrderById(final Long orderId);

    OrderResponse updateStatus(final Long orderId, final String statusValue);

    List<OrderResponse> getOrdersByUsername(final String username);

    List<OrderResponse> getOrdersByUserId(final Long userId);

    List<OrderResponse> findForUserByStatus(final String value, final Principal principal);

    List<OrderResponse> findByStatus(final String value);

    List<OrderResponse> getListOrderByCurrentUser(final Principal principal);

    OrderPageResponse getAllOrders(final int pageNo, final int pageSize);

    CheckOutDTO getDataFromUserInfor(final Principal principal);

    void paymentCompleted(final String vnp_TxnRef);

    void paymentFailed(final String vnp_TxnRef);

    OrderPageResponse filter(
            String statusValue,
            String key,
            List<String> sortCriteria,
            int pageNo,
            int pageSize
    );
}
