package com.hdshop.service.ghn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hdshop.dto.ghn.CreateGhnOrderResponse;
import com.hdshop.dto.ghn.GhnItem;
import com.hdshop.dto.ghn.GhnOrder;
import com.hdshop.entity.Address;
import com.hdshop.entity.Order;
import com.hdshop.utils.EnumPaymentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GhnServiceImpl implements GhnService {
    @Autowired
    private RestTemplate restTemplate;
    private static final String CREATE_ORDER_URL = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/create";

    @Override
    public String createGhnOrder(GhnOrder order) throws RestClientException, JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("shop_id", "192001");
        headers.set("token", "91b6b238-00c8-11ef-a6e6-e60958111f48");

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


        if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
            log.info("Status code: " + responseEntity.getStatusCode());
            log.info("Response: " + responseEntity.getBody());
            return orderCode;
        } else {
            log.error("Failed to create GHN order. Status code:" + responseEntity.getStatusCode(), responseEntity.getBody());
            return null;
        }
    }

    @Override
    public GhnOrder buildShippingOrder(Order order) {
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
                .payment_type_id(Long.valueOf(2))
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
                .service_type_id(Long.valueOf(2))
                .coupon(null)
                .items(items)
                .build();
    }
}
