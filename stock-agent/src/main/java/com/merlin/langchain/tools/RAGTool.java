package com.merlin.langchain.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

// 定义RAG检索工具
@Component
public class RAGTool {
    private static Logger log = LoggerFactory.getLogger(RAGTool.class);

    @Autowired
    private EmbeddingModel qwen3EmbeddingModel;

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;


    public TextSegment queryEmbedding(@P(value = "searchText") String searchText) {
        RAGTool.log.debug("开始执行 queryEmbedding，查询文本: {}", searchText);

        TextSegment textSegment = null;
        try {
            // 提问，并将问题转成向量数据
            Embedding queryEmbedding = qwen3EmbeddingModel.embed(searchText).content();
            // 创建搜索请求对象
            EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                    .queryEmbedding(queryEmbedding)
                    .maxResults(1) //匹配最相似的一条记录
                    // .minScore(0.8)
                    .build();

            // 根据搜索请求 searchRequest 在向量存储中进行相似度搜索
            EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);

            // searchResult.matches()：获取搜索结果中的匹配项列表。
            // .get(0)：从匹配项列表中获取第一个匹配项
            if (!searchResult.matches().isEmpty()) {
                // 获取第一个匹配项
                EmbeddingMatch<TextSegment> embeddingMatch = searchResult.matches().get(0);
                // 获取匹配项的相似度得分+返回文本结果
                RAGTool.log.debug("匹配结果详情 - 得分: {}, 内容: {}", embeddingMatch.score(), embeddingMatch.embedded().text());
                textSegment = embeddingMatch.embedded(); // 正确返回找到的TextSegment
            }


        } catch (Exception e) {
            RAGTool.log.error("embeddingSearch 执行异常: ", e);
        }
        return textSegment;
    }
}

