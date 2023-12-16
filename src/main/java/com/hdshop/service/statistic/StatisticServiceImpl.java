package com.hdshop.service.statistic;

import com.hdshop.config.DateTimeConfig;
import com.hdshop.dto.statistic.*;
import com.hdshop.dto.statistic.revenue.CountRevenueStatistic;
import com.hdshop.dto.statistic.revenue.RevenueStatistic;
import com.hdshop.repository.OrderRepository;
import com.hdshop.repository.ProductRepository;
import com.hdshop.repository.UserRepository;
import com.hdshop.service.follow.FollowService;
import com.hdshop.service.user.UserService;
import com.hdshop.utils.EnumOrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final FollowService followService;
    private final ProductRepository productRepository;

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
    public CountRevenueStatistic getCountRevenueStatistic() {
        BigDecimal countRevenue = orderRepository.getCountRevenue();
        Long countSold = orderRepository.countAllSold();
        return new CountRevenueStatistic(countRevenue, countSold);
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

    @Override
    public UserProfileStatistic getUserProfileStatistic(Principal principal) {
        String useranme = principal.getName();
        int favoriteCount = Math.toIntExact(followService.countYourFollow(principal));
        int ordered = Math.toIntExact(orderRepository.countByStatusAndUser_Username(EnumOrderStatus.ORDERED, useranme));
        int shipping = Math.toIntExact(orderRepository.countByStatusAndUser_Username(EnumOrderStatus.SHIPPING, useranme));
        int delivered = Math.toIntExact(orderRepository.countByStatusAndUser_Username(EnumOrderStatus.DELIVERED, useranme));

        UserProfileStatistic statistic = new UserProfileStatistic(
                favoriteCount, ordered, shipping, delivered
        );
        return statistic;
    }

    @Override
    public ProductSoldStatistic getProductSoldStatistic(int year) {
        long jan = productRepository.countMonthlySold(1, year);
        long feb = productRepository.countMonthlySold(2, year);
        long mar = productRepository.countMonthlySold(3, year);
        long apr = productRepository.countMonthlySold(4, year);
        long may = productRepository.countMonthlySold(5, year);
        long jun = productRepository.countMonthlySold(6, year);
        long jul = productRepository.countMonthlySold(7, year);
        long aug = productRepository.countMonthlySold(8, year);
        long sep = productRepository.countMonthlySold(9, year);
        long oct = productRepository.countMonthlySold(10, year);
        long nov = productRepository.countMonthlySold(11, year);
        long dec = productRepository.countMonthlySold(12, year);

        ProductSoldStatistic productSoldStatistic = new ProductSoldStatistic(
                jan, feb, mar, apr, may, jun, jul, aug, sep, oct, nov, dec
        );

        return productSoldStatistic;
    }

    @Override
    public RevenueStatistic getRevenueStatistic(int year) {
        BigDecimal jan = getMonthlyRevenueOrDefault(1, year);
        BigDecimal feb = getMonthlyRevenueOrDefault(2, year);
        BigDecimal mar = getMonthlyRevenueOrDefault(3, year);
        BigDecimal apr = getMonthlyRevenueOrDefault(4, year);
        BigDecimal may = getMonthlyRevenueOrDefault(5, year);
        BigDecimal jun = getMonthlyRevenueOrDefault(6, year);
        BigDecimal jul = getMonthlyRevenueOrDefault(7, year);
        BigDecimal aug = getMonthlyRevenueOrDefault(8, year);
        BigDecimal sep = getMonthlyRevenueOrDefault(9, year);
        BigDecimal oct = getMonthlyRevenueOrDefault(10, year);
        BigDecimal nov = getMonthlyRevenueOrDefault(11, year);
        BigDecimal dec = getMonthlyRevenueOrDefault(12, year);

        RevenueStatistic revenueStatistic = new RevenueStatistic(
                jan, feb, mar, apr, may, jun, jul, aug, sep, oct, nov, dec
        );

        return revenueStatistic;
    }

    private BigDecimal getMonthlyRevenueOrDefault(int month, int year) {
        BigDecimal revenue = orderRepository.getMonthlyTotalRevenue(month, year);
        return (revenue != null) ? revenue : BigDecimal.ZERO;
    }
}
