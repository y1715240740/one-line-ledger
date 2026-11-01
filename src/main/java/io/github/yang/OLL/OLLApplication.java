package io.github.yang.OLL;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类 引导类
 */

@SpringBootApplication //标识当前类是一个启动类
public class OLLApplication {
//运行这个main方法即可启动该项目
    public static void main(String[] args) {
        SpringApplication.run(OLLApplication.class, args);
    }

}
