package com.hdshop.service.order.impl;

import com.hdshop.config.DateTimeConfig;
import com.hdshop.entity.Order;
import com.hdshop.entity.OrderTracking;
import com.hdshop.exception.APIException;
import com.hdshop.exception.ResourceNotFoundException;
import com.hdshop.repository.OrderRepository;
import com.hdshop.repository.OrderTrackingRepository;
import com.hdshop.service.order.OrderTrackingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderTrackingServiceImpl implements OrderTrackingService {
    private final OrderTrackingRepository trackingRepository;
    private final OrderRepository orderRepository;
    private final MessageSource messageSource;
    private static final Logger logger = LoggerFactory.getLogger(OrderTrackingServiceImpl.class);

    @Override
    public void create(Long orderId) {
        Order order = getOrder(orderId);
        OrderTracking tracking = new OrderTracking();
        tracking.setTime(DateTimeConfig.getCurrentDateTimeInTimeZone());
        tracking.setContent(getMessage("order-successfully"));
        tracking.setOrder(order);
        try {
            trackingRepository.save(tracking);
            logger.info("order_tracking created, order_id=", orderId);
        } catch (Exception e) {
            logger.error("fail to create order_tracking, order_id=", orderId);
            throw new APIException(String.format("%s, detail: %s", getMessage("create_order_tracking_failed"), e.getStackTrace()));
        }
    }

    private Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException(getMessage("order-not-found"))
        );
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
