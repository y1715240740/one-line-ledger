package io.github.yang.OLL.service;

import io.github.yang.OLL.entity.Bill;
import io.github.yang.OLL.service.MessageSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class MessageService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final BillService billService;
    private final BudgetService budgetService;
    private final MessageSender messageSender;

    public MessageService(BillService billService,BudgetService budgetService,MessageSender messageSender) {
        this.billService = billService;
        this.budgetService = budgetService;
        this.messageSender = messageSender;

    }
    public void handle(Map<String, Object> event) {
        String text = (String) event.get("raw_message");
        if (text == null) return;

        long userId = ((Number) event.get("user_id")).longValue();
        Object groupIdObj = event.get("group_id");

// 先尝试当账单处理
        Bill bill = billService.insertByAI(text, userId);
        if (bill != null) {
            BudgetSummary s = budgetService.getSummary(userId);
            String reply = "已记 ¥" + bill.getAmount() + "  · " + bill.getCategory()
                    + "\n今日已花 " + budgetService.dailySpent(userId)  + " 元";

            if (groupIdObj != null) {
                messageSender.sendToGroup(((Number) groupIdObj).longValue(), reply);
            } else {
                messageSender.sendToUser(userId, reply);
            }
            return;
        }

        if (!"hi".equals(text)) {
            return;
        }
    }

}