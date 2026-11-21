package io.github.yang.OLL.job;

import io.github.yang.OLL.service.BudgetService;
import io.github.yang.OLL.service.BudgetSummary;
import io.github.yang.OLL.service.MessageSender;
import io.github.yang.OLL.service.WeeklyReport;
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
    @Scheduled(cron = "0 * * * * *")
    public void dailyReport(){
        BudgetSummary s = budgetService.getSummary(MY_QQ);
        String msg = "本日开销"+ budgetService.dailySpent(MY_QQ) + " 元\n本月已花 " + s.spent() + " 元\n本月还剩 " +
                s.remaining() + " 元\n日均可用 " + s.daily() + " 元";
        messageSender.sendToUser(MY_QQ,msg);
    }

    @Scheduled(cron = "0 * * * * *")
    public void weeklyReport() {
        WeeklyReport r = budgetService.weekReport(MY_QQ);

        String msg = "本周支出 " + r.total() + " 元\n"
        + "开销最大的类别：" + r.topCategory() + " " + r.topAmount() + " 元\n"
        + "上周总支出 " + r.lastTotal() + " 元";

        messageSender.sendToUser(MY_QQ, msg);
    }


}
