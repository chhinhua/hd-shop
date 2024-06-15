package com.duck.controller;

import com.duck.dto.statistic.revenue.RevenueStatistic;
import com.duck.service.statistic.StatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.Year;

@RestController
@RequestMapping("/api/v1/statistic")
public class StatisticController {
    private final StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @Operation(summary = "Get statistics number by order status")
    @GetMapping("/count")
    public ResponseEntity<?> getCount() {
        return ResponseEntity.ok(statisticService.getCountStatistic());
    }

    @Operation(summary = "Get daily order statistics")
    @GetMapping("/order/daily")
    public ResponseEntity<?> getDailyOrderStatistics(@RequestParam String date) {
        return ResponseEntity.ok(statisticService.getDailyOrder(date));
    }

    @Operation(summary = "Get monthly order statistics")
    @GetMapping("/order/monthly")
    public ResponseEntity<?> getMonthlyOrderStatistics(@RequestParam Integer month, @RequestParam Integer year) {
        return ResponseEntity.ok(statisticService.getMonthlyOrder(month, year));
    }

    @Operation(summary = "Get yearly order statistics")
    @GetMapping("/order/yearly")
    public ResponseEntity<?> getYearlyOrderStatistics(@RequestParam(name = "year", required = false) Integer year) {
        if (year == null) {
            year = Year.now().getValue();
        }
        return ResponseEntity.ok(statisticService.getYearlyOrder(year));
    }

    @Operation(summary = "Get yearly account register completed statistics")
    @GetMapping("/account/yearly")
    public ResponseEntity<?> getYearlyAccountStatistic(@RequestParam(name = "year", required = false) Integer year) {
        if (year == null) {
            year = Year.now().getValue();
        }
        return ResponseEntity.ok(statisticService.getYearlyCompleteAccount(year));
    }

    @Operation(summary = "Get yearly order completed statistics")
    @GetMapping("/order_complete/yearly")
    public ResponseEntity<?> getYearlyOrderComplete(@RequestParam(name = "year", required = false) Integer year) {
        if (year == null) {
            year = Year.now().getValue();
        }
        return ResponseEntity.ok(statisticService.getYearlyOrderComplete(year));
    }

    @Operation(summary = "Get user profile statistic")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/profile")
    public ResponseEntity<?> getUserProfileStatistic(Principal principal) {
        return ResponseEntity.ok(statisticService.getUserProfileStatistic(principal));
    }

    @Operation(summary = "Get product sold statistic")
    @GetMapping("/product/sold")
    public ResponseEntity<?> getProductSoldStatistic(@RequestParam(name = "year", required = false) Integer year) {
        if (year == null) {
            year = Year.now().getValue();
        }
        return ResponseEntity.ok(statisticService.getProductSoldStatistic(year));
    }

    @Operation(summary = "Get revenue statistic")
    @GetMapping("/revenue")
    public ResponseEntity<RevenueStatistic> getRevenueStatistic(@RequestParam(name = "year", required = false) Integer year) {
        if (year == null) {
            year = Year.now().getValue();
        }
        return ResponseEntity.ok(statisticService.getRevenueStatistic(year));
    }

    @Operation(summary = "Get count revenue & count product sold")
    @GetMapping("/revenue-count")
    public ResponseEntity<?> getCountRevenue() {
        return ResponseEntity.ok(statisticService.getCountRevenueStatistic());
    }
}
