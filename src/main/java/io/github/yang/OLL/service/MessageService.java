package io.github.yang.OLL.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class MessageService {

    private final RestTemplate restTemplate = new RestTemplate();

    public void handle(Map<String, Object> event) {
        //System.out.println("① 进入 handle");
        Object raw = event.get("raw_message");
        //System.out.println("② raw_message = [" + raw + "]");

        String text = (String) raw;
        if (!"hi".equals(text)) {
            //System.out.println("③ 不是 hi，直接返回");
            return;
        }

        long userId = ((Number) event.get("user_id")).longValue();
        Object groupIdObj = event.get("group_id");

        if(groupIdObj != null){
            long groupId = ((Number)groupIdObj).longValue();
            sendToGroup(groupId,"我收到群消息啦！");
        }else{
            send(userId, "我收到啦！");
        }

        //System.out.println("④ 准备发送给 " + userId);

        //System.out.println("⑤ 发送完成");
    }

    private void send(long userId, String msg) {
        try {
            Map<String, Object> body = Map.of(
                    "user_id", userId,
                    "message", msg
            );

            //System.out.println("⑥ 请求体 = " + body);
            //System.out.println("⑦ 开始发请求");

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