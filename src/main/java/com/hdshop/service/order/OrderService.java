package com.hdshop.service.order;

import com.hdshop.dto.order.CheckOutDTO;
import com.hdshop.dto.order.OrderDTO;
import com.hdshop.dto.order.OrderResponse;
import com.hdshop.dto.order.PageOrderResponse;
import org.springframework.data.domain.Page;

import java.security.Principal;
import java.util.List;

public interface OrderService {
    OrderResponse addOrder(final OrderDTO order, final Principal principal);

    OrderResponse createOrderFromUserCart(final OrderDTO order, final Principal principal);

    OrderResponse createOrderWithVNPay(final OrderDTO order, final String username, final String vnp_TxnRef);

    void deleteOrderById(final Long orderId);

    OrderDTO getOrderById(final Long orderId);

    OrderDTO updateStatus(final Long orderId, final String status);

    List<OrderDTO> getOrdersByUsername(final String username);

    List<OrderDTO> getOrdersByUserId(final Long userId);

    CheckOutDTO getDataFromUserInfor(final Principal principal);

    List<OrderResponse> getListOrderByCurrentUser(final Principal principal);

    void paymentCompleted(final String vnp_TxnRef);

    void paymentFailed(final String vnp_TxnRef);

    PageOrderResponse getAllOrders(final int pageNo, final int pageSize);
}
