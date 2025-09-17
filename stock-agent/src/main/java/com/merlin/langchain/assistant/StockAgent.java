package com.merlin.langchain.assistant;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(
        wiringMode = EXPLICIT
        , chatModel = "openAiChatModel"
        , chatMemoryProvider = "chatMemoryProviderStock"
        , tools = "akhareTools"
        , contentRetriever = "contentRetrieverStockPincone" //配置向量存储
)
public interface StockAgent {
    @SystemMessage(fromResource = "system-prompt-template.txt")
    String chat(@MemoryId Long memoryId, @UserMessage String userMessage);

    @SystemMessage("你是我的好朋友，请用东北话回答问题。")
    String chat2(@MemoryId Long memoryId, @UserMessage String userMessage);
}
