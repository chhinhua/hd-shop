package com.duck.dto.statistic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CountStatistic {
    private Long registeredCount;
    private Long orderedCount;
    private Long shippingCount;
    private Long deliveredCount;
}
