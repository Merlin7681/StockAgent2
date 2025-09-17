package com.merlin.langchain.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.*;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

@Component
public class AkhareTools extends AkShareCommon {
    private static final Logger log = LoggerFactory.getLogger(AkhareTools.class);
    
    @Autowired
    private EmbeddingModel qwen3EmbeddingModel;

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @Tool(name = "getStockBaseInfo", value = "根据股票代码，并返回给用户该支股票的基本面信息")
    public String getStockBaseInfo(@P(value = "股票代码") String stockCode) {

        String individualInfo = this.getStockIndividualInfo(stockCode);

       /* Token不够用，只能返回individualInfo
           String financialAbstract = this.getStockFinancialAbstract(stockCode);
           System.out.println(financialAbstract);
           return individualInfo + "\n" + financialAbstract;
       */
        return individualInfo;
    }

    /**
     * 统一接口
     * WARN：确保所有工具函数名称只包含允许的字符（字母、数字、下划线、连字符）
     *
     * @param textSegment 包含API元数据和文本内容的文本片段，用于进一步处理
     * @return 处理结果字符串
     */
    @Tool(name = "unified_api", value = "根据参数，先执行工具 embeddingSearch 寻找最适合textSegment。再将找到的textSegment作为参数传给本方法处理")
    public String unifiedInterface(@P(value = "TextSegment") TextSegment textSegment) {
        AkhareTools.log.info("开始执行 unifiedInterface，TextSegment: {}", textSegment);
        if(textSegment == null) {
            return "";
        }

        // 提取TextSegment中的api_name等元数据信息
        String apiName = textSegment.metadata().getString("api_name");
        String description = textSegment.metadata().getString("description");
        String textContent = textSegment.text();

        // 实现处理逻辑，例如根据api_name调用对应的AKShare接口
        AkhareTools.log.info("处理API: " + apiName + " - " + description);

        // 返回处理结果
        return "已成功处理接口: " + apiName + "\n" + textContent;
    }


    @Tool(name = "embeddingSearch", value = "根据参数，从向量存储中查询最匹配的TextSegment")
    public TextSegment embeddingSearch(@P(value = "searchText") String searchText) {
        AkhareTools.log.info("开始执行 embeddingSearch，查询文本: {}", searchText);

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
                AkhareTools.log.info("匹配结果详情 - 得分: {}, 内容: {}", embeddingMatch.score(), embeddingMatch.embedded().text());
                textSegment = embeddingMatch.embedded(); // 正确返回找到的TextSegment
            }


        } catch (Exception e) {
            AkhareTools.log.error("embeddingSearch 执行异常: ", e);
        }
        return textSegment;
    }
}
