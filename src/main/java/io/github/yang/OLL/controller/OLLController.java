package io.github.yang.OLL.controller;

import io.github.yang.OLL.service.MessageService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/qq")
public class OLLController {
    private final MessageService messageService;

    // 构造器注入：Spring 会自动把 MessageService 传进来
    public OLLController(MessageService messageService) {
        this.messageService = messageService;
    }
    @PostMapping("/message")
    public void receive(@RequestBody Map<String, Object> event) {//RequestBody:表示把请求体之中的JSON转化为java对象装在event之中
        messageService.handle(event);   // 自己什么都不干，全交给 Service
    }
}
