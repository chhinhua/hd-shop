package com.duck.service.ghn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.duck.dto.ghn.GhnItem;
import com.duck.dto.ghn.GhnOrder;
import com.duck.entity.Address;
import com.duck.entity.Order;
import com.duck.exception.APIException;
import com.duck.utils.EOrderStatus;
import com.duck.utils.EPaymentType;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GhnServiceImpl implements GhnService {
    @Autowired
    RestTemplate restTemplate;
    static final String SHOP_ID = "192001";
    static final String TOKEN = "91b6b238-00c8-11ef-a6e6-e60958111f48";
    static final String CANCEL_ORDER_URL = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/switch-status/cancel";
    static final String CREATE_ORDER_URL = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/create";
    static final String GET_ORDER_DETAIL_URL = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/detail";
    //static final String CALCULATE_FEE_URL = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee";

    @Override
    public String createGhnOrder(GhnOrder order) throws RestClientException, JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("shop_id", SHOP_ID);
        headers.set("token", TOKEN);

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(order);

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        // Capture the response entity
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(CREATE_ORDER_URL, entity, String.class);

        Gson gson = new Gson();
        JsonObject responseJson = gson.fromJson(responseEntity.getBody(), JsonObject.class);

        // Access the data object
        JsonObject data = responseJson.get("data").getAsJsonObject();

        // Extract the order_code
        String orderCode = data.get("order_code").getAsString();
        log.info(orderCode);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Status code: " + responseEntity.getStatusCode());
            log.info("Response: " + responseEntity.getBody());
            return orderCode;
        } else {
            log.error("Failed to create GHN order. Status code: {}, Body: {}",
                    responseEntity.getStatusCode(), responseEntity.getBody());
            return null;
        }
    }

    @Override
    public GhnOrder buildGhnOrder(Order order) {
        Address address = order.getAddress();

        List<GhnItem> items = order.getOrderItems().stream()
                .map(orderItem -> GhnItem.builder()
                        .name(orderItem.getProduct().getName())
                        .code(orderItem.getProduct().getSlug())
                        .quantity(orderItem.getQuantity().longValue())
                        .price(orderItem.getPrice().longValue())
                        .weight(100L)
                        .build())
                .collect(Collectors.toList());

        String content = items.stream().map(GhnItem::getName).collect(Collectors.joining("; "));
        String recipientAddress = address.getOrderDetails() + ", " + address.getWard() + ", " + address.getDistrict() + ", " + address.getProvince();
        Long cashOnDeliveryAmount = order.getPaymentType().equals(EPaymentType.COD) ? order.getSubTotal().longValue() : 0L;
        Long codeFailedAmount = (long) (order.getSubTotal().longValue() < 200000 ? 20000 : 30000);
        int paymentTypeId = order.getPaymentType().equals(EPaymentType.COD) ? 2 : 1; // 1. người bán trả phí ship - 2. người mua trả
        Long height = calculateHeight(items.size());

        return GhnOrder.builder()
                .note(order.getNote())
                .required_note("CHOXEMHANGKHONGTHU")
                .to_name(address.getFullName())
                .to_phone(address.getPhoneNumber())
                .to_address(recipientAddress)
                .to_ward_name(address.getWard())
                .to_district_name(address.getDistrict())
                .to_province_name(address.getProvince())
                .cod_amount(cashOnDeliveryAmount)
                .cod_failed_amount(codeFailedAmount)
                .payment_type_id(paymentTypeId)
                .weight(100L * items.size())
                .length(25L)
                .width(25L)
                .height(height)
                .insurance_value(null)
                .service_type_id(2L)
                .coupon(null)
                .content(content)
                .items(items)
                .build();
    }

    @Override
    public Long calculateHeight(int count) {
        return (long) 2 * count + 3;
    }

    @Override
    public void cancelGhnOrder(String orderCode) throws JsonProcessingException {
        // Tạo request body JSON
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("order_codes", new String[]{orderCode});

        // Tạo request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("shop_id", SHOP_ID);
        headers.add("token", TOKEN);

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(requestBody);
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(CANCEL_ORDER_URL, entity, String.class);

        // Xử lý response
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Cancel order successfuly, order_code: " + orderCode);
        } else {
            log.error("Cancel order failed, order_code: " + orderCode + responseEntity.getStatusCode());
            throw new APIException("Cancel order failed!");
        }
    }

    @Override
    public BigDecimal calculateFee(Address address) {
        return null;
    }

    @Override
    public JsonObject getOrderDetail(String orderCode) throws JsonProcessingException {
        // Tạo request body JSON
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("order_code", orderCode);

        // Tạo request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("token", TOKEN);

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(requestBody);
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(GET_ORDER_DETAIL_URL, entity, String.class);

        // Xử lý response
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            Gson gson = new Gson();
            JsonObject responseJson = gson.fromJson(responseEntity.getBody(), JsonObject.class);
            Object responseObject = responseEntity.getBody();
            log.info("Response: " + responseObject);
            return responseJson;
        } else {
            log.error("Get GHN order detail failed: " + responseEntity.getStatusCode());
            throw new APIException("Get GHN order detail failed!");
        }
    }

    @Override
    public String getOrderStatus(String orderCode) {
        try {
            JsonObject response = getOrderDetail(orderCode);
            return response.get("data").getAsJsonObject().get("status").getAsString();
        } catch (Exception e) {
            log.error("Error getting order status, order_code: " + orderCode + "Message: " + e.getMessage());
            throw new APIException("Error getting order status, order_code: " + orderCode);
        }
    }
}
