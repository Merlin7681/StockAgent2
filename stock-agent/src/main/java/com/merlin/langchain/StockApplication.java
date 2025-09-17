package com.merlin.langchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Scanner;

@SpringBootApplication
public class StockApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(StockApplication.class, args);
        
        // 启动线程监听控制台输入
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
                // 若输入 "exit"，触发关闭
                if ("exit".equals(input.trim())) {
                    System.out.println("接收到 exit 命令，关闭应用...");
                    context.close(); // 优雅关闭
                    scanner.close();
                    break;
                }
            }
        }).start();
    }

}
