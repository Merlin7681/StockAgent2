package com.merlin.langchain;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class EmbeddingTest {
    @Autowired
    private EmbeddingModel qwen3EmbeddingModel;

    @Test
    public void testEmbeddingModel() {
        Response<Embedding> embed = qwen3EmbeddingModel.embed("你好");

        System.out.println("向量维度：" + embed.content().vector().length);
        System.out.println("向量输出：" + embed.toString());
    }

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    /**
     * 将文本转换成向量，然后存储到pinecone中
     */
    // https://docs.langchain4j.dev/tutorials/embedding-stores
    @Test
    public void testPineconeEmbeded() {

        //将文本转换成向量
        TextSegment segment1 = TextSegment.from("我喜欢羽毛球");
        Embedding embedding1 = qwen3EmbeddingModel.embed(segment1).content();
        //存入向量数据库
        embeddingStore.add(embedding1, segment1);

        TextSegment segment2 = TextSegment.from("今天天气很好");
        Embedding embedding2 = qwen3EmbeddingModel.embed(segment2).content();
        embeddingStore.add(embedding2, segment2);
    }

    /**
     * Pinecone-相似度匹配
     */
    @Test
    public void embeddingSearch() {

        // 提问，并将问题转成向量数据
        Embedding queryEmbedding = qwen3EmbeddingModel.embed("你最喜欢的运动是什么？").content();
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
        EmbeddingMatch<TextSegment> embeddingMatch = searchResult.matches().get(0);

        // 获取匹配项的相似度得分
        System.out.println(embeddingMatch.score()); // 0.8144288515898701

        // 返回文本结果
        System.out.println(embeddingMatch.embedded().text());
    }

    @Test
    public void testUploadKnowledgeLibrary() {
        // 使用ClassPathResource加载resources目录下的文件
        //ClassPathResource resource1 = new ClassPathResource("knowledge/indicator.md");
        ClassPathResource resource2 = new ClassPathResource("knowledge/stock_processed.md");
        //ClassPathResource resource3 = new ClassPathResource("knowledge/tutorial.md");

        try {
            //Document document1 = FileSystemDocumentLoader.loadDocument(resource1.getFile().toPath());
            Document document2 = FileSystemDocumentLoader.loadDocument(resource2.getFile().toPath());
            //Document document3 = FileSystemDocumentLoader.loadDocument(resource3.getFile().toPath());

            //List<Document> documents = Arrays.asList(document1, document2, document3);
            List<Document> documents = Arrays.asList(document2);

            // 使用文档分割器避免单个文档过大,300字符大小，30字符重叠
            DocumentSplitter splitter = DocumentSplitters.recursive(300, 30);
            //自定义文档分割器
            //按段落分割文档：每个片段包含不超过 300个token，并且有 30个token的重叠部分保证连贯性
            //注意：当段落长度总和小于设定的最大长度时，就不会有重叠的必要。
            //DocumentByParagraphSplitter documentSplitter = new DocumentByParagraphSplitter(300, 30);

            for (Document document : documents) {
                List<TextSegment> segments = splitter.split(document);

                // 分批处理，每次处理10个segments
                int batchSize = 10;
                for (int i = 0; i < segments.size(); i += batchSize) {
                    int endIndex = Math.min(i + batchSize, segments.size());
                    List<TextSegment> batch = segments.subList(i, endIndex);

                    embeddingStore.addAll(batch.stream()
                                    .map(segment -> qwen3EmbeddingModel.embed(segment).content())
                                    .collect(Collectors.toList()),
                            batch);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
