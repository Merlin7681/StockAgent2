package com.merlin.langchain;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;


@SpringBootTest
public class FragmentEmbeddingTest {
    @Autowired
    private EmbeddingModel qwen3EmbeddingModel;

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @Test
    public void testEmbeddingModel() {
        Response<Embedding> embed = qwen3EmbeddingModel.embed("你好");

        System.out.println("向量维度：" + embed.content().vector().length);
        System.out.println("向量输出：" + embed.toString());
    }

    @Test
    public void embeddingSearch() {
        String searchStr = "股票数据总貌";
        //searchStr = "stock_individual_info_em";
        searchStr = "行情报价";
        //searchStr = "深圳证券交易所-市场总貌-地区交易排序";
        //searchStr = "股票行业成交数据";

        // 提问，并将问题转成向量数据
        Embedding queryEmbedding = qwen3EmbeddingModel.embed(searchStr).content();
        // 创建搜索请求对象
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(3) //匹配最相似的一条记录
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

    /**
     * 构建知识库（方式一）：添加textSegment（+"json"key的metadata）及其embedding.
     */
    @Test
    public void testUploadFragementKnowledge() {
        String fragmentsDirectory = "knowledge/stock_fragement";
        ClassPathResource resourceDir = new ClassPathResource(fragmentsDirectory);
        try {
            resourceDir.getFile().listFiles(file -> {
                if (file.isFile()) {
                    try {
                        File curFile = new File(file.getAbsolutePath());
                        // 如果文件后缀是json，则continue
                        if(curFile.getName().endsWith(".txt"))  {
                            String txtContent = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                            // 将文件的AbsolutePath去除后缀
                            String fileNamePrefix = curFile.getAbsolutePath().substring(0, curFile.getAbsolutePath().lastIndexOf("."));
                            // 构造一个新的文件名：比如当前文件名为 "file.txt"，则新文件名为"file_metadata.json"
                            String jsonFileName = fileNamePrefix + "_metadata.json";
                            File jsonFile = new File(jsonFileName);
                            String jsonContent = new String(java.nio.file.Files.readAllBytes(jsonFile.toPath()));
                            System.out.println("txt文件：" + file.getName());
                            //System.out.println("json文件：" + jsonContent);

                            // 创建文本片段对象，用于存储文件内容
                            TextSegment textSegment = TextSegment.from(txtContent);
                            // 将关联的JSON元数据添加到文本片段的元数据中
                            textSegment.metadata().put("json", jsonContent);
                            // 使用嵌入模型对文本片段进行向量化处理，记录向量化的时间
                            long startTime = System.currentTimeMillis();
                            //Embedding embedding = qwen3EmbeddingModel.embed(textSegment).content();
                            Embedding embedding = this.getEmbedding(textSegment);
                            long endTime = System.currentTimeMillis();
                            System.out.println("向量化耗时：" + (endTime - startTime) + "ms");
                            // 将文本内容及其对应的向量存储到向量数据库中，用于后续的相似性检索
                            //embeddingStore.add(embedding, textSegment);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 构建知识库（方式二）：添加（仅由TextContent构造的）embedding.
     */
    @Test
    public void testUploadFragementKnowledge2() {
        String fragmentsDirectory = "knowledge/stock_fragement";
        ClassPathResource resourceDir = new ClassPathResource(fragmentsDirectory);
        try {
            resourceDir.getFile().listFiles(file -> {
                if (file.isFile()) {
                    try {
                        File curFile = new File(file.getAbsolutePath());
                        // 如果文件后缀是json，则continue
                        if(curFile.getName().endsWith(".txt"))  {
                            String txtContent = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                            System.out.println("txt文件：" + file.getName());

                            // 使用嵌入模型对文本片段进行向量化处理，记录向量化的时间
                            long startTime = System.currentTimeMillis();
                            //Embedding embedding = qwen3EmbeddingModel.embed(txtContent).content();
                            TextSegment textSegment = TextSegment.from(txtContent);
                            Embedding embedding = this.getEmbedding(textSegment);
                            long endTime = System.currentTimeMillis();
                            System.out.println("向量化耗时：" + (endTime - startTime) + "ms");
                            // 将文本内容及其对应的向量存储到向量数据库中，用于后续的相似性检索
                            embeddingStore.add(embedding);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 构建知识库（方式三）：添加TextSegment（+json转key-value的metadata）及其embedding.
     */
    @Test
    public void testUploadFragementKnowledge3() {
        String fragmentsDirectory = "knowledge/stock_fragement";
        ClassPathResource resourceDir = new ClassPathResource(fragmentsDirectory);
        try {
            resourceDir.getFile().listFiles(file -> {
                if (file.isFile()) {
                    try {
                        File curFile = new File(file.getAbsolutePath());
                        // 如果文件后缀是json，则continue
                        if(curFile.getName().endsWith(".txt"))  {
                            String txtContent = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                            // 将文件的AbsolutePath去除后缀
                            String fileNamePrefix = curFile.getAbsolutePath().substring(0, curFile.getAbsolutePath().lastIndexOf("."));
                            // 构造一个新的文件名：比如当前文件名为 "file.txt"，则新文件名为"file_metadata.json"
                            String jsonFileName = fileNamePrefix + "_metadata.json";
                            File jsonFile = new File(jsonFileName);
                            String jsonContent = new String(java.nio.file.Files.readAllBytes(jsonFile.toPath()));
                            System.out.println("txt文件：" + file.getName());
                            //System.out.println("json文件：" + jsonContent);

                            // 创建文本片段对象，用于存储文件内容
                            TextSegment textSegment = TextSegment.from(txtContent);
                            /* 将jsonContent转为Json对象，形如
                                {
                                  "fragment_id": 1,
                                  "category": "A股 > 股票市场总貌 > 上海证券交易所",
                                  "api_name": "stock_sse_summary",
                                  "description": "上海证券交易所-股票数据总貌",
                                  "has_input_parameters": false,
                                  "has_output_parameters": true,
                                  "input_parameters_count": 0,
                                  "output_parameters_count": 4
                                }
                            */
                            JsonObject jsonObject = new Gson().fromJson(jsonContent, JsonObject.class);
                            textSegment.metadata().put("fragment_id", jsonObject.get("fragment_id").getAsInt());
                            textSegment.metadata().put("category", jsonObject.get("category").getAsString());
                            textSegment.metadata().put("api_name", jsonObject.get("api_name").getAsString());
                            textSegment.metadata().put("description", jsonObject.get("description").getAsString());
                            textSegment.metadata().put("has_input_parameters", jsonObject.get("has_input_parameters").getAsString());
                            textSegment.metadata().put("has_output_parameters", jsonObject.get("has_output_parameters").getAsString());
                            textSegment.metadata().put("input_parameters_count", jsonObject.get("input_parameters_count").getAsInt());
                            textSegment.metadata().put("output_parameters_count", jsonObject.get("output_parameters_count").getAsInt());

                            // 使用嵌入模型对文本片段进行向量化处理，记录向量化的时间
                            long startTime = System.currentTimeMillis();
                            Embedding embedding = this.getEmbedding(textSegment);
                            long endTime = System.currentTimeMillis();
                            System.out.println("向量化耗时：" + (endTime - startTime) + "ms");
                            // 将文本内容及其对应的向量存储到向量数据库中，用于后续的相似性检索
                            embeddingStore.add(embedding, textSegment);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected Embedding getEmbedding(TextSegment textSegment) {
        Embedding embedding = null;
        try {
            // 添加重试机制
            int maxRetries = 3;
            for (int i = 0; i < maxRetries; i++) {
                try {
                    embedding = qwen3EmbeddingModel.embed(textSegment).content();
                    break; // 成功则跳出循环
                } catch (Exception e) {
                    System.out.println("第 " + (i + 1) + " 次尝试失败: " + e.getMessage());
                    if (i == maxRetries - 1) {
                        throw e; // 最后一次尝试仍然失败，抛出异常
                    }
                    // 等待一段时间后重试
                    Thread.sleep(2000);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return embedding;
    }

}
