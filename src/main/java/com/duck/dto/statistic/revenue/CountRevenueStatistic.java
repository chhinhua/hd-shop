package com.duck.dto.statistic.revenue;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CountRevenueStatistic {
    private BigDecimal countRevenue;
    private Long countSold;
}
