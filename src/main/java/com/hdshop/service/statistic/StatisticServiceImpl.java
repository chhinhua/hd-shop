package com.hdshop.service.statistic;

import com.hdshop.config.DateTimeConfig;
import com.hdshop.dto.statistic.AccountStatistic;
import com.hdshop.dto.statistic.CompleteOrderStatistic;
import com.hdshop.dto.statistic.CountStatistic;
import com.hdshop.dto.statistic.OrderStatistic;
import com.hdshop.repository.OrderRepository;
import com.hdshop.repository.UserRepository;
import com.hdshop.utils.EnumOrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    public CountStatistic getCountStatistic() {
        CountStatistic countStatistic = new CountStatistic();

        // Đếm số lượng người dùng đã đăng ký
        Long registeredCount = userRepository.count();
        countStatistic.setRegisteredCount(registeredCount);

        // Đếm số lượng đơn hàng đã được tạo
        Long orderedCount = orderRepository.count();
        countStatistic.setOrderedCount(orderedCount);

        // Đếm số lượng đơn hàng đã được giao hàng
        Long shippingCount = orderRepository.countByStatus(EnumOrderStatus.SHIPPING);
        countStatistic.setShippingCount(shippingCount);

        // Đếm số lượng đơn hàng đã hoàn thành
        Long deliveredCount = orderRepository.countByStatus(EnumOrderStatus.DELIVERED);
        countStatistic.setDeliveredCount(deliveredCount);

        return countStatistic;
    }

    @Override
    public OrderStatistic getDailyOrder(String dateValue) {
        ZonedDateTime zonedDateTime = DateTimeConfig.parseDateTime(dateValue);
        LocalDate localDate = zonedDateTime.toLocalDate();

        long wait_for_pay = orderRepository.countByDate(localDate, EnumOrderStatus.WAIT_FOR_PAY);
        long ordered = orderRepository.countByDate(localDate, EnumOrderStatus.ORDERED);
        long processing = orderRepository.countByDate(localDate, EnumOrderStatus.PROCESSING);
        long shipping = orderRepository.countByDate(localDate, EnumOrderStatus.SHIPPING);
        long delivered = orderRepository.countByDate(localDate, EnumOrderStatus.DELIVERED);
        long cancaled = orderRepository.countByDate(localDate, EnumOrderStatus.CANCELED);

        OrderStatistic dailyStatistic = new OrderStatistic(
                wait_for_pay,
                ordered,
                processing,
                shipping,
                delivered,
                cancaled
        );

        return dailyStatistic;
    }

    @Override
    public OrderStatistic getMonthlyOrder(int month, int year) {
        long wait_for_pay = orderRepository.countByMonthAndYear(month, year, EnumOrderStatus.WAIT_FOR_PAY);
        long ordered = orderRepository.countByMonthAndYear(month, year, EnumOrderStatus.ORDERED);
        long processing = orderRepository.countByMonthAndYear(month, year, EnumOrderStatus.PROCESSING);
        long shipping = orderRepository.countByMonthAndYear(month, year, EnumOrderStatus.SHIPPING);
        long delivered = orderRepository.countByMonthAndYear(month, year, EnumOrderStatus.DELIVERED);
        long cancaled = orderRepository.countByMonthAndYear(month, year, EnumOrderStatus.CANCELED);

        OrderStatistic monthlyStatistic = new OrderStatistic(
                wait_for_pay,
                ordered,
                processing,
                shipping,
                delivered,
                cancaled
        );

        return monthlyStatistic;
    }

    @Override
    public OrderStatistic getYearlyOrder(int year) {
        long wait_for_pay = orderRepository.countByYear(year, EnumOrderStatus.WAIT_FOR_PAY);
        long ordered = orderRepository.countByYear(year, EnumOrderStatus.ORDERED);
        long processing = orderRepository.countByYear(year, EnumOrderStatus.PROCESSING);
        long shipping = orderRepository.countByYear(year, EnumOrderStatus.SHIPPING);
        long delivered = orderRepository.countByYear(year, EnumOrderStatus.DELIVERED);
        long cancaled = orderRepository.countByYear(year, EnumOrderStatus.CANCELED);

        OrderStatistic yearlyStatistic = new OrderStatistic(
                wait_for_pay,
                ordered,
                processing,
                shipping,
                delivered,
                cancaled
        );

        return yearlyStatistic;
    }
    @Override
    public AccountStatistic getYearlyCompleteAccount(int year) {
        long jan = userRepository.getMonthlyUserCounts(1, year);
        long feb = userRepository.getMonthlyUserCounts(2, year);
        long mar = userRepository.getMonthlyUserCounts(3, year);
        long apr = userRepository.getMonthlyUserCounts(4, year);
        long may = userRepository.getMonthlyUserCounts(5, year);
        long jun = userRepository.getMonthlyUserCounts(6, year);
        long jul = userRepository.getMonthlyUserCounts(7, year);
        long aug = userRepository.getMonthlyUserCounts(8, year);
        long sep = userRepository.getMonthlyUserCounts(9, year);
        long oct = userRepository.getMonthlyUserCounts(10, year);
        long nov = userRepository.getMonthlyUserCounts(11, year);
        long dec = userRepository.getMonthlyUserCounts(12, year);

        AccountStatistic accountStatistic = new AccountStatistic(
                jan, feb, mar, apr, may, jun, jul, aug, sep, oct, nov, dec
        );

        return accountStatistic;
    }

    @Override
    public CompleteOrderStatistic getYearlyOrderComplete(int year) {
        long jan = orderRepository.getMonthlyOrderComplete(1, year);
        long feb = orderRepository.getMonthlyOrderComplete(2, year);
        long mar = orderRepository.getMonthlyOrderComplete(3, year);
        long apr = orderRepository.getMonthlyOrderComplete(4, year);
        long may = orderRepository.getMonthlyOrderComplete(5, year);
        long jun = orderRepository.getMonthlyOrderComplete(6, year);
        long jul = orderRepository.getMonthlyOrderComplete(7, year);
        long aug = orderRepository.getMonthlyOrderComplete(8, year);
        long sep = orderRepository.getMonthlyOrderComplete(9, year);
        long oct = orderRepository.getMonthlyOrderComplete(10, year);
        long nov = orderRepository.getMonthlyOrderComplete(11, year);
        long dec = orderRepository.getMonthlyOrderComplete(12, year);

        CompleteOrderStatistic completeOrderStatistic = new CompleteOrderStatistic(
                jan, feb, mar, apr, may, jun, jul, aug, sep, oct, nov, dec
        );

        return completeOrderStatistic;
    }
}
