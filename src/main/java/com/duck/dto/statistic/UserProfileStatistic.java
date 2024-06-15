package com.duck.dto.statistic;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileStatistic {
    private Integer favoriteCount;
    private Integer ordered;
    private Integer shipping;
    private Integer delivered;
}
