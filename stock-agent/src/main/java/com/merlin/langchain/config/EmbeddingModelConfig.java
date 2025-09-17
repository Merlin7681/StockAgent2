package com.merlin.langchain.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import dev.langchain4j.http.client.spring.restclient.SpringRestClientBuilderFactory;

import java.time.Duration;

@Configuration
public class EmbeddingModelConfig {

    @Bean
    public EmbeddingModel qwen3EmbeddingModel() {
        SpringRestClientBuilderFactory factory = new SpringRestClientBuilderFactory();
        return OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("dengcao/Qwen3-Embedding-0.6B:Q8_0")
                .timeout(Duration.ofMinutes(5)) // 增加超时时间
                .httpClientBuilder(factory.create()) // 修复：使用正确的create()方法
                .build();
    }


}
