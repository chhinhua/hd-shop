package com.hdshop.service.ghn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hdshop.dto.ghn.GhnItem;
import com.hdshop.dto.ghn.GhnOrder;
import com.hdshop.entity.Address;
import com.hdshop.entity.Order;
import com.hdshop.exception.APIException;
import com.hdshop.utils.EnumOrderStatus;
import com.hdshop.utils.EnumPaymentType;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

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
    static final String CANCEL_ORDER_URL = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/switch-status/cancel";
    static final String CREATE_ORDER_URL = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/create";
    static final String GET_ORDER_DETAIL_URL = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/detail";
    static final String SHOP_ID = "192001";
    static final String TOKEN = "91b6b238-00c8-11ef-a6e6-e60958111f48";

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
                        .weight(200L)
                        .build())
                .collect(Collectors.toList());

        return GhnOrder.builder()
                .payment_type_id(2L)
                .note(order.getNote())
                .required_note("CHOXEMHANGKHONGTHU")
                .to_name(address.getFullName())
                .to_phone(address.getPhoneNumber())
                .to_address(address.getOrderDetails() + address.getWard() + address.getDistrict() + address.getCity())
                .to_ward_name(address.getWard())
                .to_district_name(address.getDistrict())
                .to_province_name(address.getCity())
                .cod_amount(order.getPaymentType().equals(EnumPaymentType.COD) ? order.getTotal().longValue() : 0L)
                .weight(200L * items.size())  // TODO write method to calculate weight length width height from order item data
                .length(25L * items.size())
                .width(25L * items.size())
                .height(7L * items.size())
                .cod_failed_amount(order.getTotal().longValue())
                .insurance_value(null)
                .service_type_id(2L)
                .coupon(null)
                .items(items)
                .build();
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

        // Capture the response entity
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(CANCEL_ORDER_URL, entity, String.class);

        // Xử lý response
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Cancel order successfuly, order_code: " + orderCode);
        } else {
            log.error("Cancel order failed, order_code: " + orderCode + responseEntity.getStatusCode());
        }
    }

    @Override
    public EnumOrderStatus getEnumStatus(String ghnOrderStatus) {
        switch (ghnOrderStatus) {
            case "ready_to_pick":
                return EnumOrderStatus.ORDERED; // đã đặt/mới tạo đơn
            case "picking":
            case "money_collect_picking":
            case "picked":
            case "storing":
            case "sorting":
            case "transporting":
                return EnumOrderStatus.PROCESSING; // đang xử lý
            case "delivering":
            case "money_collect_delivering":
                return EnumOrderStatus.SHIPPING; // đang giao
            case "delivered":
                return EnumOrderStatus.DELIVERED; // đã giao/hoàn thành
            case "waiting_to_return":
            case "return":
            case "return_transporting":
            case "return_sorting":
            case "returning":
            case "return_fail":
            case "returned":
                return EnumOrderStatus.RETURN_REFUND; // trả hàng/hoàn tiền
            case "cancel":
                return EnumOrderStatus.CANCELED; // hủy
            case "exception":
            case "damage":
            case "lost":
                return null;
            default:
                log.error("Unexpected value: " + ghnOrderStatus);
                return null;
        }
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

        // Capture the response entity
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
            return null;
        }
    }

    @Override
    public String getOrderStatus(String orderCode) {
        try {
            JsonObject response = getOrderDetail(orderCode);
            return response.get("data").getAsJsonObject().get("status").getAsString();
        } catch (Exception e) {
            log.error("Error getting order status, order_code: " + orderCode + "Message: " + e.getMessage());
            return null;
        }
    }
}
