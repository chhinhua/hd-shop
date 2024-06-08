package com.hdshop.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hdshop.dto.order.OrderPageResponse;
import com.hdshop.dto.order.OrderResponse;

public interface RedisOrderService {
    void clear();

    void saveMyOrders(OrderPageResponse response,
                      String statusValue,
                      String keySearch,
                      int pageNo,
                      int pageSize,
                      String username) throws JsonProcessingException;

    OrderPageResponse getMyOrders(String statusValue,
                                  String keySearch,
                                  int pageNo,
                                  int pageSize,
                                  String username) throws JsonProcessingException;
}
