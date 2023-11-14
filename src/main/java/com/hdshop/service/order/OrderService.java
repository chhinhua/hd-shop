package com.hdshop.service.order;

import com.hdshop.dto.order.CheckOutDTO;
import com.hdshop.dto.order.OrderDTO;
import com.hdshop.dto.order.OrderResponse;

import java.security.Principal;
import java.util.List;

public interface OrderService {
    OrderResponse addOrder(final OrderDTO order, final Principal principal);

    OrderResponse addOrderFromUserCart(final OrderDTO order, final Principal principal);

    void deleteOrderById(final Long orderId);

    OrderDTO getOrderById(final Long orderId);

    OrderDTO updateStatus(final Long orderId, final String status);

    List<OrderDTO> getOrdersByUsername(final String username);

    List<OrderDTO> getOrdersByUserId(final Long userId);

    CheckOutDTO getDataFromUserInfor(final Principal principal);

    List<OrderResponse> getListOrderByCurrentUser(Principal principal);
}
