package io.github.yang.OLL.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.yang.OLL.entity.Bill;
import io.github.yang.OLL.mapper.BillMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BudgetService {

    // 先写死一个月度预算，以后从数据库读
    private static final BigDecimal MONTH_BUDGET = new BigDecimal("3000");

    private final BillMapper billMapper;

    public BudgetService(BillMapper billMapper) {
        this.billMapper = billMapper;
    }

    public BigDecimal sumBetween(long userId, LocalDateTime start, LocalDateTime end) {
        QueryWrapper<Bill> qw = new QueryWrapper<>();
        qw.eq("user_id", userId)
                .ge("occurred_at", start)
                .lt("occurred_at", end);

        List<Bill>list = billMapper.selectList(qw);

        BigDecimal sum=BigDecimal.ZERO;
        for(Bill b:list){
            sum=sum.add(b.getAmount());
        }
        return sum;
    }

    //本日开销
    public BigDecimal dailySpent(long userId){
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return sumBetween(userId, start, end);
    }

    // 本月已花
    public BigDecimal monthSpent(long userId) {
        LocalDateTime start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = start.plusMonths(1);
        return sumBetween(userId, start, end);
    }

    // 本月剩余
    public BigDecimal remaining(long userId) {
        return MONTH_BUDGET.subtract(monthSpent(userId));
    }

    // 日均可用
    public BigDecimal dailyAllowance(long userId) {
        LocalDate today = LocalDate.now();
        int daysLeft = today.lengthOfMonth() - today.getDayOfMonth() + 1;
        return remaining(userId)
                .divide(new BigDecimal(daysLeft), 2, RoundingMode.HALF_UP);
    }
}
