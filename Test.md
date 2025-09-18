
# 智能体测试

## 小黑的问题列表
请提供603676股票的基本面信息
03202股票现在买入合适吗？
请提供603202股票的基本面信息
请基于603202和603676两支股票的基本面信息，对比两只股票；

请使用 每日股票情况API 查询当日股票情况；stock_sse_deal_daily

### 竟然让成功了？
请提供 当月的股票行业成交数据；stock_szse_sector_summary
请获取 股票300959的实时变动 stock_hot_rank_detail_realtime_em
请提供603676的 个股-股票信息；stock_individual_info_em


## 智能体
### 问题：MCP如何RAG和Tools联动？
请问通过 langchain for Java 怎么实现一个 MCP 让它与rag的知识库和外部工具AKTools之间进行联动呢？
提示：AKTools is a package of HTTP API for AKShare!
1、知识库RAG准备：已经向量数据库中已经添加了AKShare API信息（按api接口进行了分片，并添加了metadata）；
2、外部工具准备：本地已经部署了AKTools，可以通过http://127.0.0.1:8080/api/public/加URL后缀（方法名和参数）构造URL并访问 ，如http://127.0.0.1:8080/api/public/stock_profit_sheet_by_quarterly_em?symbol=SH600000；
3、langchain4j @Tools：已经通过@Tools注解，注册了一个通用工具，只要依据不同API接口输入URL后缀，即可构造出完整URL并访问；
现在请提供一个MCP，让MCP与 rag 和 AKTools进行联动，并给出一个完整的测试用例。

## 注意事项
### OpenAI Demo模型限制    
无API key前提下，每次访问OpenAI API（仅gpt-4o-mini免费可用），最多可以消耗5000个token。
~~~
# http://langchain4j.dev/demo/openai/v1
# gpt-4o-mini
Maximum number of tokens per request for demonstration purposes is 5000. If you wish to use more, please use your own OpenAI API key.
~~~

### Ollama 模型
~~~shell
# 检查 Ollama 是否运行
ollama list
# List running models
ollama ps

# 如果没有运行，启动 Ollama 服务
ollama serve
# 或者在后台运行
ollama serve &
~~~

