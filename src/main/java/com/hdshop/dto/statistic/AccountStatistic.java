package com.hdshop.dto.statistic;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountStatistic {
    private long jan;
    private long feb;
    private long mar;
    private long apr;
    private long may;
    private long jun;
    private long jul;
    private long aug;
    private long sep;
    private long oct;
    private long nov;
    private long dec;
}
