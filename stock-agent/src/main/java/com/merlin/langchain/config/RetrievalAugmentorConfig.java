package com.merlin.langchain.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 检索增强器（暂时没有用）
 */
@Configuration
public class RetrievalAugmentorConfig {
    @Autowired
    private EmbeddingModel qwen3EmbeddingModel;

    @Autowired
    private ContentRetriever contentRetrieverStockPincone;

    @Bean
    RetrievalAugmentor retrievalAugmentor() {
        return DefaultRetrievalAugmentor.builder()
                //.queryRouter()
                //.queryTransformer()
                .contentRetriever(contentRetrieverStockPincone)
                .build();
    }
}
