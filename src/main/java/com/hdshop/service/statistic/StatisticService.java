package com.hdshop.service.statistic;

import com.hdshop.dto.statistic.*;

import java.security.Principal;

public interface StatisticService {
    CountStatistic getCountStatistic();

    OrderStatistic getDailyOrder(String date);

    OrderStatistic getMonthlyOrder(int month, int year);

    OrderStatistic getYearlyOrder(int year);

    AccountStatistic getYearlyCompleteAccount(int year);

    CompleteOrderStatistic getYearlyOrderComplete(int year);

    UserProfileStatistic getUserProfileStatistic(final Principal principal);

    ProductSoldStatistic getProductSoldStatistic(int year);

    RevenueStatistic getRevenueStatistic(int year);
}
