# 智能体-小黑聊股票



## 项目概述

### 目的

​		学习大语言模型和智能体基础知识，包括LangChain4j,  LLM, MCP, Embedding Model, Embedding Storage, MCP, Tools等。



**LangChain4j功能**

![LangChain4j功能介绍](images/langchain_function.png)

### 部署架构

**部署架构**
![部署架构示意图](images/overview.png)


## 外部工具
### AkTools
- 构建参考：项目KnowledgeBase/Common/stock/aktools
- 启动参考：startup-backend.sh
- 接口列表参考：stock_processed.md

服务示例：
~~~
利润表-按单季度 http://127.0.0.1:8080/api/public/stock_profit_sheet_by_quarterly_em?symbol=SH600000

http://localhost:8080/api/public/stock_individual_info_em?symbol=603676
http://localhost:8080/api/public/stock_financial_abstract?symbol=603676
http://127.0.0.1:8080/api/public/stock_szse_sector_summary?symbol=当月
~~~


## 知识库
### 数据向量化
1. 数据清洗：将akshare中的stock.md内容进行清洗，只保留文档结构、接口名、描述、输入参数和输出参数，并保存为stock_processed.md
2. 数据标注：将stock_processed.md按照接口进行分片（即一个方法一个分片），在stock_fragement目录中生成分片文件和原数据；
3. 数据向量化存储：遍历所有分片，将每个分片进行向量化，并保存到向量数据库中。参考FragmentEmbeddingTest.testUploadFragementKnowledge3()；

### Embedding模型和存储
向量模型用的是qwen3-embedding-0.6b，向量存储用的是Pinecone。
~~~shell
# 确保 Ollama 服务已启动（默认监听http://localhost:11434）
ollama pull dengcao/Qwen3-Embedding-0.6B:Q8_0
ollama run dengcao/Qwen3-Embedding-0.6B:Q8_0
~~~

## 其他基础知识

### 前端接口测试
https://mvnrepository.com/
~~~xml
<!-- 前后端分离中的后端接口测试工具 -->
<dependency>
      <groupId>com.github.xiaoymin</groupId>
      <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
      <version>${knife4j.version}</version>
</dependency>
~~~
http://localhost/doc.html


### 常用Maven命令
~~~shell
cd stock-agent
lsof -i :80
# 查看项目依赖
mvn dependency:list
# 使用 Maven 的 spring-boot:run 命令运行应用程序
mvn spring-boot:run
mvn spring-boot:run -e
# 使用 Maven 编译项目
mvn compile -X
mvn package -X
~~~


## 参考资料
- [Atlas Vector Search与 LangChain4j 集成](https://www.mongodb.com/zh-cn/docs/atlas/ai-integrations/langchain4j/)
- https://docs.langchain4j.dev/category/tutorials
- 