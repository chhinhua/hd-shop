package com.hdshop.controller;

import com.hdshop.config.VNPayConfig;
import com.hdshop.dto.order.OrderDTO;
import com.hdshop.repository.OrderRepository;
import com.hdshop.service.order.OrderService;
import com.hdshop.service.vnpay.VNPayService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Duration;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/vnpay")
public class VNPayController {
    private final VNPayService vnPayService;
    private final MessageSource messageSource;
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @GetMapping("")
    public String home() {
        return "index";
    }

    @GetMapping("/submit-order")
    public String submitOrder(@RequestParam("amount") BigDecimal orderTotal,
                              @RequestParam("orderInfo") String orderInfo,
                              HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(orderTotal, orderInfo, baseUrl);
        return "redirect:" + vnpayUrl;
    }

    @Transactional
    @PostMapping("/submit-order")
    public String submitOrderV2(@RequestBody OrderDTO order,
                                Principal principal,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        String orderInfor = messageSource.getMessage("pay-for-purchases-on-duck-shop", null, LocaleContextHolder.getLocale());
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(order.getTotal(), orderInfor, baseUrl);

        System.out.println(vnpayUrl);

        orderService.createOrderWithVNPay(order, principal, VNPayConfig.vnp_TxnRef);

        return "redirect:" + vnpayUrl;
    }

    @GetMapping("/vnpay-payment")
    public String GetMapping(HttpServletRequest request, Model model) {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");

        model.addAttribute("orderId", orderInfo);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);

        // TODO lưu transaction
        // TODO xóa items trong giỏ hàng

        if (paymentStatus == 1) {
            orderService.paymentCompleted(vnp_TxnRef);
        } else {
            //orderService.paymentFailed(vnp_TxnRef);
        }

        return paymentStatus == 1 ? "ordersuccess" : "orderfail";
    }
}
