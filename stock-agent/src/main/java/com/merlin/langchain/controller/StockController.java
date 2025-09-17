package com.merlin.langchain.controller;

import com.merlin.langchain.assistant.StockAgent;
import com.merlin.langchain.bean.ChatForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Tag(name = "小助手")
@RestController
@RequestMapping("/")
public class StockController {
    Logger log = LoggerFactory.getLogger(StockController.class);

    @Autowired
    private StockAgent stockAgent;

    @Operation(summary = "对话")
    @PostMapping("/chat")
    public String chat(@RequestBody ChatForm chatForm) {
        try {
            return stockAgent.chat(chatForm.getMemoryId(), chatForm.getMessage());
        } catch (dev.langchain4j.exception.TimeoutException e) {
            // 记录日志并返回友好的错误信息
            log.error("请求超时，memoryId: {}, message: {}", chatForm.getMemoryId(), chatForm.getMessage(), e);
            return "请求处理超时，请稍后重试";
        }
    }

    @PostMapping("/chatAsync")
    public CompletableFuture<String> chatAsync(@RequestBody ChatForm chatForm) {
        return CompletableFuture.supplyAsync(() ->
                        stockAgent.chat(chatForm.getMemoryId(), chatForm.getMessage())
                ).orTimeout(60, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    log.error("处理聊天请求时发生错误", throwable);
                    return "请求处理失败，请稍后重试";
                });
    }
}
