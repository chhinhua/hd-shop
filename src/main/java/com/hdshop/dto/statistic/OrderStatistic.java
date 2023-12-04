package com.hdshop.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderStatistic {
        private Long wait_for_pay;
        private Long ordered;
        private Long processing;
        private Long shipping;
        private Long deliveried;
        private Long canceled;
}
