package com.hdshop.service.statistic;

import com.hdshop.dto.statistic.AccountStatistic;
import com.hdshop.dto.statistic.CountStatistic;
import com.hdshop.dto.statistic.OrderStatistic;

public interface StatisticService {
    CountStatistic getCountStatistic();

    OrderStatistic getDailyOrder(String date);

    OrderStatistic getMonthlyOrder(int month, int year);

    OrderStatistic getYearlyOrder(int year);

    AccountStatistic getYearlyAccount(int year);
}
