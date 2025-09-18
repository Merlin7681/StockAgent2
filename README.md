
## 前端接口测试
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


## Embedding 模型
~~~
ollama pull dengcao/Qwen3-Embedding-0.6B:Q8_0
#ollama run dengcao/Qwen3-Embedding-0.6B:Q8_0
# 确保 Ollama 服务已启动（默认监听http://localhost:11434）
~~~

## 常用Maven命令
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

## AkTools服务示例
利润表-按单季度
http://127.0.0.1:8080/api/public/stock_profit_sheet_by_quarterly_em?symbol=SH600000

http://localhost:8080/api/public/stock_individual_info_em?symbol=603676

http://localhost:8080/api/public/stock_financial_abstract?symbol=603676
http://127.0.0.1:8080/api/public/stock_szse_sector_summary?symbol=当月

## 参考资料
- [Atlas Vector Search与 LangChain4j 集成](https://www.mongodb.com/zh-cn/docs/atlas/ai-integrations/langchain4j/)
- https://docs.langchain4j.dev/category/tutorials
- 