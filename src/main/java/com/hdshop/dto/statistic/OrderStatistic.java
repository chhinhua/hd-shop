package com.hdshop.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatistic {
        private Long wait_for_pay;
        private Long ordered;
        private Long processing;
        private Long shipping;
        private Long deliveried;
        private Long canceled;
}
