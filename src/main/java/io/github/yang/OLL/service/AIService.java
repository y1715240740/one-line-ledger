package io.github.yang.OLL.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    @Value("${zhipu.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public AIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public String chat(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> body = Map.of(
                "model", "glm-4-flash",
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.1
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        //把请求头和请求体封装到HttpEntity中

        return restTemplate.postForObject(
                "https://open.bigmodel.cn/api/paas/v4/chat/completions",
                entity,//发送的内容：请求体
                String.class//指定返回类型为String
        );
    }

    public Map<String, Object> parseBill(String text) throws Exception {
        String prompt = "你是记账助手。判断下面这句话是不是一笔消费，只返回 JSON，不要任何解释。\n"
        + "如果是消费，返回：{\"isBill\":true,\"amount\":25.5,\"category\":\"餐饮\",\"note\":\"午饭\"}\n"
        + "如果不是消费，返回：{\"isBill\":false}\n"
        + "category 只能从 餐饮、交通、购物、娱乐、其他 中选一个。\n"
        + "句子：" + text;
        //把输入的语句扔给ai 然后嵌套上面这一层话 用户的话拼在结尾

        String resp = chat(prompt); //返回值是JSON字符串

        JsonNode root = MAPPER.readTree(resp);
        //readTree 把一段 JSON 文本变成可以按字段访问的对象。

        String content = root.path("choices").get(0).path("message").path("content").asText();
        content = content.replace("```json", "").replace("```", "").trim();
        //清楚代码块标记

        JsonNode node = MAPPER.readTree(content);
        //这一步把文本变成一个树形结构，每个字段变成一个节点，有名字、能按名字找到。但是内部还是知道是各自的数据类型
        Map<String, Object> result = new HashMap<>();
        result.put("isBill", node.path("isBill").asBoolean(false));
        result.put("amount", node.path("amount").decimalValue());
        result.put("category", node.path("category").asText("其他"));
        result.put("note", node.path("note").asText(text));
        //你调用哪个方法，决定按什么类型取出
        //️asBoolean() → 按布尔值取
        //asText() → 按字符串取
        //asInt()、decimalValue() → 按数字取
        return result;
    }
}