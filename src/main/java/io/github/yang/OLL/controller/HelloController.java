//package io.github.yang.qqbot.controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import tools.jackson.databind.ObjectMapper;
//
//import java.util.Map;
//
//@RestController //接口类：专门接待HTTP请求
//@RequestMapping("/qq")//这个类所有接口的前缀
//public class HelloController {
//
//    private static final ObjectMapper MAPPER = new ObjectMapper();
//
//    @PostMapping("/message")//具体路径 只接收POST请求
//    public void receive(@RequestBody Map<String, Object> event) throws Exception {
//        //把json转换为map： json只是数据格式 而map是对象 让人更方便的取值
//
//        System.out.println("收到消息:"+event.get("raw_message"));
//
//        Object userId = event.get("user_id");
//        Object msg = event.get("message");
//        System.out.println("QQ号: " + userId + " 发了: " + msg);
//
//        System.out.println(event);//直接打印map 如果你想把它发给前端，前端是认不出这种格式的，前端只认标准 JSON。
//
//        System.out.println(MAPPER.writerWithDefaultPrettyPrinter()
//                .writeValueAsString(event));}//把内存里的 Map 对象，重新“序列化”成了JSON文本。
//        //这叫手动转 JSON，通常只用来打印日志（Log）排查 Bug。但不需要手动转springboot会自动转【
//
//    /*收快递：NapCat 发来一坨 JSON（纯文本字符串）。
//    拆快递：Spring Boot 自动把它转成 Map（方便你 get 取数据，这个过程叫反序列化）。
//    干活：你提取 raw_message，去记账、调 AI。
//    打包：你把处理结果（比如“记账成功”），再打包成 JSON（这个过程叫序列化）。
//    发快递：返回给 NapCat，NapCat 再把它变成 QQ 气泡发到你的手机上。*/
//}
//
///*
//@RestController//标识当前类是一个请求处理类
//    public class HelloController {//名字都叫xxController
//        // 之前的 Hello World
//
//        // 定义一个方法去接收前端发起的请求 也可以用requestMapping
//        @GetMapping("/hello")//请求路径：和需求保持一致
//        public String hello() {
//            return "Hello World";//前端相应一个字符串
//        }
//
//        // 今天要写的：接收参数
//        @GetMapping("/greet")//标识当前方法处理/greet请求 标识请求路径 识别到了就会执行方法
//        public String greet(@RequestParam String name) {//申明一个参数
//            return "你好，" + name + "！欢迎进入后端世界。";
//        }
//
//       // 这是一个 POST 接口
//        @PostMapping("/qq")
//        public String receiveQQMessage(@RequestBody String message) {
//            // 在控制台打印一下，看看有没有收到
//            System.out.println("收到QQ消息: " + message);
//            return "后端已收到！你发的是：" + message;
//        }*/
