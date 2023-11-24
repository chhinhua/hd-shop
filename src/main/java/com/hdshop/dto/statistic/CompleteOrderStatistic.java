package com.hdshop.dto.statistic;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class CompleteOrderStatistic {
    private Integer day;
    private Integer month;
    private Integer year;
    private Long jan;
    private Long feb;
    private Long mar;
    private Long apr;
    private Long may;
    private Long jun;
    private Long aug;
    private Long sep;
    private Long oct;
    private Long nov;
    private Long dec;
}
