package io.github.yang.OLL.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.yang.OLL.entity.Bill;
import io.github.yang.OLL.mapper.BillMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BudgetService {

    // 先写死一个月度预算，以后从数据库读
    private static final BigDecimal MONTH_BUDGET = new BigDecimal("3000");

    private final BillMapper billMapper;

    public BudgetService(BillMapper billMapper) {
        this.billMapper = billMapper;
    }

    public List<Bill> billsBetween(long userId, LocalDateTime start, LocalDateTime end) {
        QueryWrapper<Bill> qw = new QueryWrapper<>();
        qw.eq("user_id", userId)
                .ge("occurred_at", start)
                .lt("occurred_at", end);
        return billMapper.selectList(qw);
    }

    //计算两个时间段之间的账单总和
    public BigDecimal sumBetween(long userId, LocalDateTime start, LocalDateTime end) {
        BigDecimal sum=BigDecimal.ZERO;
        for(Bill b:billsBetween(userId, start, end)){
            sum=sum.add(b.getAmount());
        }
        return sum;
    }
    public BigDecimal dailySpent(long userId) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return sumBetween(userId, start, end);
    }
    //计算每日消费、剩余预算、日均预算
    public BudgetSummary getSummary(long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.withDayOfMonth(1).atStartOfDay();

        BigDecimal spent = sumBetween(userId, start, start.plusMonths(1));
        BigDecimal remain = MONTH_BUDGET.subtract(spent);

        int daysLeft = today.lengthOfMonth() - today.getDayOfMonth() + 1;
        BigDecimal daily = remain.divide(new BigDecimal(daysLeft), 2, RoundingMode.HALF_UP);

        return new BudgetSummary(spent, remain, daily);
    }

    public WeeklyReport weekReport(long userId) {
        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDateTime start = monday.atStartOfDay();
        LocalDateTime end = start.plusWeeks(1);

        List<Bill> thisWeek = billsBetween(userId, start, end);
        List<Bill> lastWeek = billsBetween(userId, start.minusWeeks(1), start);

        BigDecimal total = BigDecimal.ZERO;
        Map<String, BigDecimal> byCategory = new HashMap<>();
        //记录每个类别的开销

        for (Bill b : thisWeek) {
            total = total.add(b.getAmount());//累加本周开销
            byCategory.merge(b.getCategory(), b.getAmount(), BigDecimal::add);
            //byCategory.merge(键, 新值, 合并函数)
        }

        BigDecimal lastTotal = BigDecimal.ZERO;
        for (Bill b : lastWeek) {
            lastTotal = lastTotal.add(b.getAmount());
        }

        String topCategory = "无";
        BigDecimal topAmount = BigDecimal.ZERO;
        for (Map.Entry<String, BigDecimal> e : byCategory.entrySet()) {
            //遍历键值对的时候必须用entrySet()：把map变成一组“键值对” 因为map本身不能被遍历
            //Map.Entry取一行数据，表示一个键值对
            if (e.getValue().compareTo(topAmount) > 0) {
                topCategory = e.getKey();
                topAmount = e.getValue();
            }
        }

        return new WeeklyReport(total, lastTotal, topCategory, topAmount);
    }

}
