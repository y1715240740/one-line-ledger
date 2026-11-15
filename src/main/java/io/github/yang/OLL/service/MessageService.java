package io.github.yang.OLL.service;

import io.github.yang.OLL.entity.Bill;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class MessageService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final BillService billService;

    public MessageService(BillService billService) {
        this.billService = billService;
    }
    public void handle(Map<String, Object> event) {
        String text = (String) event.get("raw_message");
        if (text == null) return;

        long userId = ((Number) event.get("user_id")).longValue();
        Object groupIdObj = event.get("group_id");

// 先尝试当账单处理
        Bill bill = billService.insertFromText(text, userId);
        if (bill != null) {
            String reply = "已记 ¥" + bill.getAmount() + " " + bill.getCategory();
            if (groupIdObj != null) {
                sendToGroup(((Number) groupIdObj).longValue(), reply);
            } else {
                sendToUser(userId, reply);
            }
            return;
        }

        if (!"hi".equals(text)) {
            return;
        }

    }

    private void sendToUser(long userId, String msg) {
        try {
            Map<String, Object> body = Map.of(
                    "user_id", userId,
                    "message", msg
            );
            String resp = restTemplate.postForObject(//快递小哥 把地址和数据给他 会自动帮我发送
                    "http://127.0.0.1:3001/send_msg",
                    body,
                    String.class
            );

            System.out.println(" 发送结果 = " + resp);

        } catch (Exception e) {
            System.out.println("❌ 发送异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendToGroup(long groupId,String msg){
        try{
            Map<String,Object>body = Map.of(
                    "group_id",groupId,
                    "message",msg
            );

            String resp=restTemplate.postForObject(
                    "http://127.0.0.1:3001/send_msg",
                    body,
                    String.class
            );
            System.out.println("群发送结果 = "+ resp);
        }
        catch (Exception e){
            System.out.println("❌ 群发送异常：" + e.getMessage());
        }
    }
}