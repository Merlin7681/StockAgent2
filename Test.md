
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
### RAG与Tools联动
向量数据库中已经添加了api相关信息（参考src/main/resources/knowledge/stock_fragement内文件和FragmentEmbeddingTest.testUploadFragementKnowledge3()方法），请问AkhareTools.unifiedInterface()方法的@Tool.value应如何写提示词，才可以把向量数据库中找到的TesxtSegment信息当作参数传给AkhareTools.unifiedInterface()方法？

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

