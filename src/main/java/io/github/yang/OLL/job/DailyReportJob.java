package io.github.yang.OLL.job;

import io.github.yang.OLL.service.BudgetService;
import io.github.yang.OLL.service.MessageSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;

@Component
public class DailyReportJob {

    private static final long MY_QQ = 1715240740L;

    private final MessageSender messageSender;
    private final BudgetService budgetService;

    public DailyReportJob(MessageSender messageSender, BudgetService budgetService) {
        this.messageSender = messageSender;
        this.budgetService = budgetService;
    }
    @Scheduled(cron = "0 30 21 * * *")
    public void dailyReport(){
        BigDecimal today = budgetService.dailySpent(MY_QQ);
        BigDecimal remaining = budgetService.remaining(MY_QQ);
        BigDecimal dailyAllowance = budgetService.dailyAllowance(MY_QQ);

        String msg = "今日支出" + today + "元\n本月剩余" + remaining + "元\n日均可用" + dailyAllowance + "元";

        messageSender.sendToUser(MY_QQ,msg);
    }
}
