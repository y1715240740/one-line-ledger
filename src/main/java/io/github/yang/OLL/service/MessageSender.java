package io.github.yang.OLL.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class MessageSender {

    private final RestTemplate restTemplate;
    public MessageSender(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendToUser(long userId, String msg) {
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

    public void sendToGroup(long groupId,String msg){
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
