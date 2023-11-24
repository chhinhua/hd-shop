package com.hdshop.dto.statistic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CountStatistic {
    private Long registeredCount;
    private Long orderedCount;
    private Long shippingCount;
    private Long deliveredCount;
}
