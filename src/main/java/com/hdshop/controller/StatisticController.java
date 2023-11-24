package com.hdshop.controller;

import com.hdshop.service.statistic.StatisticService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statistic")
public class StatisticController {
    private final StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping("/count")
    public ResponseEntity<?> getCount() {
        return ResponseEntity.ok(statisticService.getCountStatistic());
    }

    @GetMapping("/order/daily")
    public ResponseEntity<?> getDailyStatistics(@RequestParam String date) {
        return ResponseEntity.ok(statisticService.getDailyOrder(date));
    }

    @GetMapping("/order/monthly")
    public ResponseEntity<?> getMonthlyStatistics(@RequestParam int month,
                                               @RequestParam int year) {
        return ResponseEntity.ok(statisticService.getMonthlyOrder(month, year));
    }

    @GetMapping("/order/yearly")
    public ResponseEntity<?> getYearlyStatistics(@RequestParam int year) {
        return ResponseEntity.ok(statisticService.getYearlyOrder(year));
    }

    @GetMapping("/account/yearly")
    public ResponseEntity<?> getYearlyAccountStatistic(@RequestParam int year) {
        return ResponseEntity.ok(statisticService.getYearlyAccount(year));
    }
}
