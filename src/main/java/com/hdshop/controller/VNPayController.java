package com.hdshop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hdshop.config.VNPayConfig;
import com.hdshop.dto.order.OrderDTO;
import com.hdshop.entity.Order;
import com.hdshop.service.order.OrderService;
import com.hdshop.service.vnpay.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/vnpay")
@Slf4j
public class VNPayController {
    private final VNPayService vnPayService;
    private final MessageSource messageSource;
    private final OrderService orderService;

    @GetMapping("")
    public String home() {
        return "index";
    }

    @GetMapping("/submit-order-v2")
    public String submitOrderV2(@RequestParam("orderId") Long orderId, HttpServletRequest request) {
        Order order = orderService.findById(orderId);

        String orderInfor = messageSource.getMessage("pay-for-purchases-on-duck-shop", null, LocaleContextHolder.getLocale());
        String vnp_TxnRef = order.getId().toString();
        String baseUrl = null;

        if (request != null) {
            baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        }

        String vnpayUrl = vnPayService.createOrder(order.getTotal(), orderInfor, baseUrl, vnp_TxnRef);
        log.info("vnpay url: " + vnpayUrl);

        // follow order
        orderService.createWithVNPayV2(order.getId(), vnp_TxnRef);

        return "redirect:" + vnpayUrl;
    }

    @GetMapping("/submit-order")
    public String submitOrder(@RequestParam("amount") BigDecimal orderTotal,
                              @RequestParam("addressId") Long addressId,
                              @RequestParam("username") String username,
                              @RequestParam(name = "note", required = false) String note,
                              HttpServletRequest request) {
        String orderInfor = messageSource.getMessage("pay-for-purchases-on-duck-shop", null, LocaleContextHolder.getLocale());
        String baseUrl = null;
        if (request != null) {
            baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        }
        String vnpayUrl = vnPayService.createOrder(orderTotal, orderInfor, baseUrl);
        log.info("vnpay url: " + vnpayUrl);

        String decodedNote = URLDecoder.decode(note, StandardCharsets.UTF_8);

        // build order from request
        OrderDTO order = new OrderDTO();
        order.setNote(decodedNote);
        order.setAddressId(addressId);
        order.setPaymentType("VN_PAY");
        order.setTotal(orderTotal);

        // follow order
        orderService.createWithVNPay(order, username, VNPayConfig.vnp_TxnRef);

        return "redirect:" + vnpayUrl;
    }

    @GetMapping("/pay")
    public String makePayment(@RequestParam("amount") BigDecimal orderTotal,
                              @RequestParam("addressId") Long addressId,
                              @RequestParam("orderId") Long orderId,
                              @RequestParam(name = "note", required = false) String note,
                              HttpServletRequest request) {
        String orderInfor = messageSource.getMessage("pay-for-purchases-on-duck-shop", null, LocaleContextHolder.getLocale());
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnp_TxnRef = orderId.toString();
        String vnpayUrl = vnPayService.createOrder(orderTotal, orderInfor, baseUrl, vnp_TxnRef);
        log.info(vnpayUrl);

        String decodedNote = URLDecoder.decode(note, StandardCharsets.UTF_8);

        // build order from request
        OrderDTO order = new OrderDTO();
        order.setId(orderId);
        order.setNote(decodedNote);
        order.setAddressId(addressId);
        order.setTotal(orderTotal);

        // make payment
        orderService.makePaymentForVNPAY(order);

        return "redirect:" + vnpayUrl;
    }

    @GetMapping("/vnpay-payment")
    public String GetMapping(HttpServletRequest request, Model model) throws JsonProcessingException {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");

        // Định dạng chuỗi ngày giờ thành LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime formatedPaymentTime = LocalDateTime.parse(paymentTime, formatter);

        String formattedTotalPrice = totalPrice;
        try {
            double totalPriceValue = Double.parseDouble(totalPrice) / 100;
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            formattedTotalPrice = currencyFormat.format(totalPriceValue);
        } catch (NumberFormatException ignored) {
        }

        model.addAttribute("orderId", orderInfo);
        model.addAttribute("totalPrice", formattedTotalPrice);
        model.addAttribute("paymentTime", formatedPaymentTime);
        model.addAttribute("transactionId", transactionId);

        if (paymentStatus == 1) {
            orderService.paymentCompleted(vnp_TxnRef);
        }  //orderService.paymentFailed(vnp_TxnRef);

        return paymentStatus == 1 ? "ordersuccess" : "orderfail";
    }
}
