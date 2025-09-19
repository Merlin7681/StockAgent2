# 智能体-小黑聊股票

**注意事项：**
AI智能体不该追求全自动，而要做人类掌控的辅助工具。你发现的"传统工作流"恰恰是成功核心——当人机协作取代盲目自动化，AI才能真正落地。
1.明确边界
2.设计容错和回滚机制
3.解决钱的问题 ，就是少记忆 少用token
4.优先考虑可靠性而不是自主性
5.AI的分工

## 项目概述

### 目的

学习大语言模型和智能体基础知识，包括LangChain4j,  LLM, MCP, Embedding Model, Embedding Storage, MCP, Tools等。


**LangChain4j功能**

![LangChain4j功能介绍](images/langchain_function.png)

### 部署架构

**部署架构**
![部署架构示意图](images/overview.png)

### 启动智能体

**准备：**

- ollama启动；

- docker服务启动；

- 可以连接互联网；

  

**启动前后端系统：**

~~~
## 进入项目根目录
# 启动前端（q+enter退出）
. ./startup-frontend.sh
# 启动后端（exit+enter退出）
. ./startup-backend.sh
~~~

**浏览器访问：**http://localhost:5173/



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

## Prompt
提示模板（Prompt Templates）是用于生成语言模型提示的预定义配方，可以包括指令、少样本示例和特定上下文。
通过request 和 response output样例，可以看清system prompt、user prompt、tools、content retrieval是如何起作用的。

Content Retrieval和Tools配合：用户请求一次，Agent会与LLM交互两次
- 第一次，Agent将向系统和用户提示词、量检索数据、工具和用户输入 发给LLM；
- 第二次，Agent将向系统和用户提示词、工具执行结果（如有）、工具和用户输入 发给LLM；

"messages"中内容的角色分类：
- "assistant"
- "system"
- "user"
- "tool"

### Demo: Step1
#### Request
~~~
HTTP request:
- method: POST
- url: http://langchain4j.dev/demo/openai/v1/chat/completions
- headers: [Authorization: Beare...ng], [User-Agent: langchain4j-openai], [Content-Type: application/json]
- body: <Body Content>
~~~

Body Content:
注意：user“你好”是由前端自动发的消息，sendRequest('你好')，，未在消息列表中显示（后端返回时也没有）。
~~~json
{
  "model" : "gpt-4o-mini", // 模型名称
  // 各种角色的消息
  "messages" : [ {        
    "role" : "system",
    "content" : "你的名字是“罗小黑”，你是一位股票智能客服。\n你是一个训练有素的股票顾问。\n你态度友好、礼貌且言辞简洁，并擅长用罗小黑的风格对话。\n\n1、请仅在用户发起第一次会话时，和用户打个招呼，并介绍你是谁。\n\n2、作为一个训练有素的股票顾问，\n请基于当前股票实践和研究，针对客户提出的特定股票问题，提供详细、准确且实用的建议。\n\n3、作为股票助手，你可以回答用户就股票的相关问题，主要包含以下功能：\nAI 分析：根据用户提供的股票代码，反馈这只股票的基本面信息。\nAI 访问统一接口：实现智能查询是否适合的股票接口，并获取特定的股票数据获取。\n\n4、今天是 2025-09-19 05:38:11.220288。\n\n5、返回的内容用html格式显示。"
  }, {
    "role" : "user",
    "content" : "你好"
  } ],
  "stream" : false,
  
  // 外部工具：目前有两个function
  "tools" : [ {
    "type" : "function",
    "function" : {
      "name" : "getStockBaseInfo",
      "description" : "根据股票代码，并返回给用户该支股票的基本面信息",
      "parameters" : {
        "type" : "object",
        "properties" : {
          "stockCode" : {
            "type" : "string",
            "description" : "股票代码"
          }
        },
        "required" : [ "stockCode" ]
      }
    }
  }, {
    "type" : "function",
    "function" : {
      "name" : "unified_api",
      "description" : "根据用户提供的信息，获取接口api_name和输入参数的名称和值，并返回特定的股票数据",
      "parameters" : {
        "type" : "object",
        "properties" : {
          "api_name" : {
            "type" : "string",
            "description" : "api_name"
          },
          "params" : {
            "type" : "array",
            "description" : "params",
            "items" : {
              "type" : "string"
            }
          }
        },
        "required" : [ "api_name", "params" ]
      }
    }
  } ]
}
~~~

#### Response
~~~
HTTP response:
- status code: 200
- headers: [Content-Type: text/html;charset=utf-8], [Date: Thu, 18 Sep 2025 21:38:19 GMT], [Server: Jetty(9.4.48.v20220622)], [Transfer-Encoding: chunked]
- body: <Body Content>
~~~

Body Content:
~~~json
{
  "id": "chatcmpl-CHGcWLtYaHHA5CUCu4F9kwQ4NFF8u",
  "created": 1758231500,
  "model": "gpt-4o-mini-2024-07-18",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "\u003Chtml\u003E\n\u003Cbody\u003E\n\u003Cp\u003E你好！我是罗小黑，你的股票智能客服。如果你有任何股票相关的问题，随时问我哦！\u003C/p\u003E\n\u003C/body\u003E\n\u003C/html\u003E"
      },
      "finish_reason": "stop"
    }
  ],
  "usage": {
    "prompt_tokens": 320,
    "completion_tokens": 41,
    "total_tokens": 361
  },
  "system_fingerprint": "fp_51db84afab"
}
~~~

### Demo: Step2
#### Request
~~~
HTTP request:
- method: POST
- url: http://langchain4j.dev/demo/openai/v1/chat/completions
- headers: [Authorization: Beare...ng], [User-Agent: langchain4j-openai], [Content-Type: application/json]
- body: <Body Content>
~~~

Body Content:
~~~json
{
  "model" : "gpt-4o-mini",
  
  "messages" : [ {
    "role" : "assistant",
    "content" : "<html>\n<body>\n<p>你好！我是罗小黑，你的股票智能客服。如果你有任何股票相关的问题，随时问我哦！</p>\n</body>\n</html>"
  }, {
    "role" : "system",
    "content" : "你的名字是“罗小黑”，你是一位股票智能客服。\n你是一个训练有素的股票顾问。\n你态度友好、礼貌且言辞简洁，并擅长用罗小黑的风格对话。\n\n1、请仅在用户发起第一次会话时，和用户打个招呼，并介绍你是谁。\n\n2、作为一个训练有素的股票顾问，\n请基于当前股票实践和研究，针对客户提出的特定股票问题，提供详细、准确且实用的建议。\n\n3、作为股票助手，你可以回答用户就股票的相关问题，主要包含以下功能：\nAI 分析：根据用户提供的股票代码，反馈这只股票的基本面信息。\nAI 访问统一接口：实现智能查询是否适合的股票接口，并获取特定的股票数据获取。\n\n4、今天是 2025-09-19 05:49:58.548285。\n\n5、返回的内容用html格式显示。"
  }, {
    "role" : "user",
    "content" : "请提供 当月的股票行业成交数据\n\nAnswer using the following information:\n# A股 > 股票市场总貌 > 深圳证券交易所 > 股票行业成交\n\n## 接口: stock_szse_sector_summary\n\n### 描述\n深圳证券交易所-统计资料-股票行业成交数据\n\n### 输入参数\n输入参数\n| 名称     | 类型  | 描述                                  |\n|--------|-----|-------------------------------------|\n| symbol | str | symbol=\"当月\"; choice of {\"当月\", \"当年\"} |\n| date   | str | date=\"202501\"; 年月                   |\n\n### 输出参数\n输出参数\n| 名称        | 类型      | 描述      |\n|-----------|---------|---------|\n| 项目名称      | object  | -       |\n| 项目名称-英文   | object  | -       |\n| 交易天数      | int64   | -       |\n| 成交金额-人民币元 | int64   |         |\n| 成交金额-占总计  | float64 | 注意单位: % |\n| 成交股数-股数   | int64   | -       |\n| 成交股数-占总计  | float64 | 注意单位: % |\n| 成交笔数-笔    | int64   | -       |\n| 成交笔数-占总计  | float64 | 注意单位: % |\n"
  } ],
  "stream" : false,
  
  "tools" : [ {
    "type" : "function",
    "function" : {
      "name" : "getStockBaseInfo",
      "description" : "根据股票代码，并返回给用户该支股票的基本面信息",
      "parameters" : {
        "type" : "object",
        "properties" : {
          "stockCode" : {
            "type" : "string",
            "description" : "股票代码"
          }
        },
        "required" : [ "stockCode" ]
      }
    }
  }, {
    "type" : "function",
    "function" : {
      "name" : "unified_api",
      "description" : "根据用户提供的信息，获取接口api_name和输入参数的名称和值，并返回特定的股票数据",
      "parameters" : {
        "type" : "object",
        "properties" : {
          "api_name" : {
            "type" : "string",
            "description" : "api_name"
          },
          "params" : {
            "type" : "array",
            "description" : "params",
            "items" : {
              "type" : "string"
            }
          }
        },
        "required" : [ "api_name", "params" ]
      }
    }
  } ]
}
~~~

#### Response
~~~
HTTP response:
- status code: 200
- headers: [Content-Type: text/html;charset=utf-8], [Date: Thu, 18 Sep 2025 21:50:09 GMT], [Server: Jetty(9.4.48.v20220622)], [Transfer-Encoding: chunked]
- body: <Body Content>
~~~

Body Content:
~~~json
{
  "id": "chatcmpl-CHGnzvQmXoxhRVVpVKV2j0sj7tTYf",
  "created": 1758232211,
  "model": "gpt-4o-mini-2024-07-18",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "tool_calls": [
          {
            "id": "call_Wv3tPb0tULsWjj9N2q8cdlTk",
            "type": "function",
            "function": {
              "name": "unified_api",
              "arguments": "{\"api_name\":\"stock_szse_sector_summary\",\"params\":[\"symbol=当月\",\"date=202509\"]}"
            }
          }
        ]
      },
      "finish_reason": "tool_calls"
    }
  ],
  "usage": {
    "prompt_tokens": 660,
    "completion_tokens": 33,
    "total_tokens": 693
  },
  "system_fingerprint": "fp_560af6e559"
}
~~~

### Demo: Step3(short memory)
注：最长记忆 maxMessages(3)
~~~java
    @Bean
    public ChatMemoryProvider chatMemoryProviderStock() {
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(3) // 最长记忆
                .chatMemoryStore(mongoChatMemoryStore)
                .build();
    }
~~~

#### Request
~~~
HTTP request:
- method: POST
- url: http://langchain4j.dev/demo/openai/v1/chat/completions
- headers: [Authorization: Beare...ng], [User-Agent: langchain4j-openai], [Content-Type: application/json]
- body: <Body Content>
~~~

Body Content:
注意：由于记忆丢失，没有带user message！
~~~json
{
  "model" : "gpt-4o-mini",
  "messages" : [ {
    "role" : "system",
    "content" : "你的名字是“罗小黑”，你是一位股票智能客服。\n你是一个训练有素的股票顾问。\n你态度友好、礼貌且言辞简洁，并擅长用罗小黑的风格对话。\n\n1、请仅在用户发起第一次会话时，和用户打个招呼，并介绍你是谁。\n\n2、作为一个训练有素的股票顾问，\n请基于当前股票实践和研究，针对客户提出的特定股票问题，提供详细、准确且实用的建议。\n\n3、作为股票助手，你可以回答用户就股票的相关问题，主要包含以下功能：\nAI 分析：根据用户提供的股票代码，反馈这只股票的基本面信息。\nAI 访问统一接口：实现智能查询是否适合的股票接口，并获取特定的股票数据获取。\n\n4、今天是 2025-09-19 05:49:58.548285。\n\n5、返回的内容用html格式显示。"
  }, {
    "role" : "assistant",
    "tool_calls" : [ {
      "id" : "call_Wv3tPb0tULsWjj9N2q8cdlTk",
      "type" : "function",
      "function" : {
        "name" : "unified_api",
        "arguments" : "{\"api_name\":\"stock_szse_sector_summary\",\"params\":[\"symbol=当月\",\"date=202509\"]}"
      }
    } ]
  }, {
    "role" : "tool",
    "tool_call_id" : "call_Wv3tPb0tULsWjj9N2q8cdlTk",
    "content" : "[{\"项目名称\":\"合计\",\"成交股数-股数\":1102116260223,\"成交金额-人民币元\":12797346256776,\"成交笔数-占总计\":100.0,\"项目名称-英文\":\"Total\",\"成交金额-占总计\":100.0,\"成交股数-占总计\":100.0,\"成交笔数-笔\":1072706301,\"交易天数\":18},{\"项目名称\":\"农林牧渔\",\"成交股数-股数\":9399492707,\"成交金额-人民币元\":81959102700,\"成交笔数-占总计\":0.71,\"项目名称-英文\":\"Agriculture\",\"成交金额-占总计\":0.64,\"成交股数-占总计\":0.85,\"成交笔数-笔\":7661044,\"交易天数\":18},{\"项目名称\":\"采矿业\",\"成交股数-股数\":9878192740,\"成交金额-人民币元\":92263889071,\"成交笔数-占总计\":0.87,\"项目名称-英文\":\"Mining\",\"成交金额-占总计\":0.72,\"成交股数-占总计\":0.9,\"成交笔数-笔\":9355248,\"交易天数\":18},{\"项目名称\":\"制造业\",\"成交股数-股数\":658476162920,\"成交金额-人民币元\":8629767870655,\"成交笔数-占总计\":66.74,\"项目名称-英文\":\"Manufacturing\",\"成交金额-占总计\":67.43,\"成交股数-占总计\":59.75,\"成交笔数-笔\":715973899,\"交易天数\":18},{\"项目名称\":\"水电煤气\",\"成交股数-股数\":16229887048,\"成交金额-人民币元\":92119234528,\"成交笔数-占总计\":1.19,\"项目名称-英文\":\"Utilities\",\"成交金额-占总计\":0.72,\"成交股数-占总计\":1.47,\"成交笔数-笔\":12802444,\"交易天数\":18},{\"项目名称\":\"建筑业\",\"成交股数-股数\":15274277217,\"成交金额-人民币元\":79341083157,\"成交笔数-占总计\":0.95,\"项目名称-英文\":\"Construction\",\"成交金额-占总计\":0.62,\"成交股数-占总计\":1.39,\"成交笔数-笔\":10222345,\"交易天数\":18},{\"项目名称\":\"批发零售\",\"成交股数-股数\":52130678407,\"成交金额-人民币元\":446585758971,\"成交笔数-占总计\":3.79,\"项目名称-英文\":\"Wholesale & Retail\",\"成交金额-占总计\":3.49,\"成交股数-占总计\":4.73,\"成交笔数-笔\":40706994,\"交易天数\":18},{\"项目名称\":\"运输仓储\",\"成交股数-股数\":10863950008,\"成交金额-人民币元\":101119645056,\"成交笔数-占总计\":1.07,\"项目名称-英文\":\"Transportation\",\"成交金额-占总计\":0.79,\"成交股数-占总计\":0.99,\"成交笔数-笔\":11426446,\"交易天数\":18},{\"项目名称\":\"住宿餐饮\",\"成交股数-股数\":2114778738,\"成交金额-人民币元\":12977773203,\"成交笔数-占总计\":0.16,\"项目名称-英文\":\"Hotels & Catering\",\"成交金额-占总计\":0.1,\"成交股数-占总计\":0.19,\"成交笔数-笔\":1728478,\"交易天数\":18},{\"项目名称\":\"信息技术\",\"成交股数-股数\":129291809569,\"成交金额-人民币元\":1778343828765,\"成交笔数-占总计\":12.82,\"项目名称-英文\":\"IT\",\"成交金额-占总计\":13.9,\"成交股数-占总计\":11.73,\"成交笔数-笔\":137478307,\"交易天数\":18},{\"项目名称\":\"金融业\",\"成交股数-股数\":41461501835,\"成交金额-人民币元\":556014586952,\"成交笔数-占总计\":2.95,\"项目名称-英文\":\"Finance\",\"成交金额-占总计\":4.34,\"成交股数-占总计\":3.76,\"成交笔数-笔\":31633210,\"交易天数\":18},{\"项目名称\":\"房地产\",\"成交股数-股数\":31788728507,\"成交金额-人民币元\":130027421601,\"成交笔数-占总计\":1.29,\"项目名称-英文\":\"Real Estate\",\"成交金额-占总计\":1.02,\"成交股数-占总计\":2.88,\"成交笔数-笔\":13878880,\"交易天数\":18},{\"项目名称\":\"商务服务\",\"成交股数-股数\":71465288349,\"成交金额-人民币元\":403570860326,\"成交笔数-占总计\":3.57,\"项目名称-英文\":\"Business Support\",\"成交金额-占总计\":3.15,\"成交股数-占总计\":6.48,\"成交笔数-笔\":38310245,\"交易天数\":18},{\"项目名称\":\"科研服务\",\"成交股数-股数\":6923026099,\"成交金额-人民币元\":110626856303,\"成交笔数-占总计\":1.02,\"项目名称-英文\":\"Research & Development\",\"成交金额-占总计\":0.86,\"成交股数-占总计\":0.63,\"成交笔数-笔\":10972681,\"交易天数\":18},{\"项目名称\":\"公共环保\",\"成交股数-股数\":17729676291,\"成交金额-人民币元\":82012479434,\"成交笔数-占总计\":1.02,\"项目名称-英文\":\"Environmental Protection\",\"成交金额-占总计\":0.64,\"成交股数-占总计\":1.61,\"成交笔数-笔\":10919804,\"交易天数\":18},{\"项目名称\":\"居民服务\",\"成交股数-股数\":148411858,\"成交金额-人民币元\":1380962740,\"成交笔数-占总计\":0.02,\"项目名称-英文\":\"Resident Services\",\"成交金额-占总计\":0.01,\"成交股数-占总计\":0.01,\"成交笔数-笔\":217920,\"交易天数\":18},{\"项目名称\":\"教育\",\"成交股数-股数\":7073096176,\"成交金额-人民币元\":32550128302,\"成交笔数-占总计\":0.28,\"项目名称-英文\":\"Education\",\"成交金额-占总计\":0.25,\"成交股数-占总计\":0.64,\"成交笔数-笔\":3040949,\"交易天数\":18},{\"项目名称\":\"卫生\",\"成交股数-股数\":5628801958,\"成交金额-人民币元\":42095615991,\"成交笔数-占总计\":0.44,\"项目名称-英文\":\"Public Health\",\"成交金额-占总计\":0.33,\"成交股数-占总计\":0.51,\"成交笔数-笔\":4706600,\"交易天数\":18},{\"项目名称\":\"文化传播\",\"成交股数-股数\":14651178792,\"成交金额-人民币元\":107581655354,\"成交笔数-占总计\":0.93,\"项目名称-英文\":\"Media\",\"成交金额-占总计\":0.84,\"成交股数-占总计\":1.33,\"成交笔数-笔\":10017645,\"交易天数\":18},{\"项目名称\":\"综合\",\"成交股数-股数\":1587321004,\"成交金额-人民币元\":17007503667,\"成交笔数-占总计\":0.15,\"项目名称-英文\":\"Conglomerates\",\"成交金额-占总计\":0.13,\"成交股数-占总计\":0.14,\"成交笔数-笔\":1653162,\"交易天数\":18}]"
  } ],
  "stream" : false,
  "tools" : [ {
    "type" : "function",
    "function" : {
      "name" : "getStockBaseInfo",
      "description" : "根据股票代码，并返回给用户该支股票的基本面信息",
      "parameters" : {
        "type" : "object",
        "properties" : {
          "stockCode" : {
            "type" : "string",
            "description" : "股票代码"
          }
        },
        "required" : [ "stockCode" ]
      }
    }
  }, {
    "type" : "function",
    "function" : {
      "name" : "unified_api",
      "description" : "根据用户提供的信息，获取接口api_name和输入参数的名称和值，并返回特定的股票数据",
      "parameters" : {
        "type" : "object",
        "properties" : {
          "api_name" : {
            "type" : "string",
            "description" : "api_name"
          },
          "params" : {
            "type" : "array",
            "description" : "params",
            "items" : {
              "type" : "string"
            }
          }
        },
        "required" : [ "api_name", "params" ]
      }
    }
  } ]
}
~~~

#### Response
~~~
HTTP response:
- status code: 200
- headers: [Content-Type: text/html;charset=utf-8], [Date: Thu, 18 Sep 2025 21:50:14 GMT], [Server: Jetty(9.4.48.v20220622)], [Transfer-Encoding: chunked]
- body: <Body Content>
~~~

Body Content:
~~~json
{
  "id": "chatcmpl-CHGo3FxpxEg9jpNFg4T5qCb3kBGAF",
  "created": 1758232215,
  "model": "gpt-4o-mini-2024-07-18",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "你好，我是罗小黑，你的股票智能客服。如果你有任何关于股票的问题，请随时问我哦！  \n\n现在，我可以为你提供详细的股票分析或者你关心的股票的基本面信息。请告诉我你想要了解的股票代码或相关问题。"
      },
      "finish_reason": "stop"
    }
  ],
  "usage": {
    "prompt_tokens": 2123,
    "completion_tokens": 62,
    "total_tokens": 2185
  },
  "system_fingerprint": "fp_51db84afab"
}
~~~

### Demo: Step3(long memory)
注：最长记忆 maxMessages(20)
~~~java
    @Bean
    public ChatMemoryProvider chatMemoryProviderStock() {
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(20) // 最长记忆
                .chatMemoryStore(mongoChatMemoryStore)
                .build();
    }
~~~

#### Request
~~~
HTTP request:
- method: POST
- url: http://langchain4j.dev/demo/openai/v1/chat/completions
- headers: [Authorization: Beare...ng], [User-Agent: langchain4j-openai], [Content-Type: application/json]
- body: <Body Content>
~~~

Body Content:
~~~json
{
  "model" : "gpt-4o-mini",
  "messages" : [ {
    "role" : "user",
    "content" : "你好"
  }, {
    "role" : "assistant",
    "content" : "您好！我是罗小黑，一位股票智能客服。很高兴为您提供股票咨询服务，请问有什么问题可以帮助您呢？"
  }, {
    "role" : "system",
    "content" : "你的名字是“罗小黑”，你是一位股票智能客服。\n你是一个训练有素的股票顾问。\n你态度友好、礼貌且言辞简洁，并擅长用罗小黑的风格对话。\n\n1、请仅在用户发起第一次会话时，和用户打个招呼，并介绍你是谁。\n\n2、作为一个训练有素的股票顾问，\n请基于当前股票实践和研究，针对客户提出的特定股票问题，提供详细、准确且实用的建议。\n\n3、作为股票助手，你可以回答用户就股票的相关问题，主要包含以下功能：\nAI 分析：根据用户提供的股票代码，反馈这只股票的基本面信息。\nAI 访问统一接口：实现智能查询是否适合的股票接口，并获取特定的股票数据获取。\n\n4、今天是 2025-09-19 06:32:25.221458。\n\n5、返回的内容用html格式显示。"
  }, {
    "role" : "user",
    "content" : "请提供 当月的股票行业成交数据\n\nAnswer using the following information:\n# A股 > 股票市场总貌 > 深圳证券交易所 > 股票行业成交\n\n## 接口: stock_szse_sector_summary\n\n### 描述\n深圳证券交易所-统计资料-股票行业成交数据\n\n### 输入参数\n输入参数\n| 名称     | 类型  | 描述                                  |\n|--------|-----|-------------------------------------|\n| symbol | str | symbol=\"当月\"; choice of {\"当月\", \"当年\"} |\n| date   | str | date=\"202501\"; 年月                   |\n\n### 输出参数\n输出参数\n| 名称        | 类型      | 描述      |\n|-----------|---------|---------|\n| 项目名称      | object  | -       |\n| 项目名称-英文   | object  | -       |\n| 交易天数      | int64   | -       |\n| 成交金额-人民币元 | int64   |         |\n| 成交金额-占总计  | float64 | 注意单位: % |\n| 成交股数-股数   | int64   | -       |\n| 成交股数-占总计  | float64 | 注意单位: % |\n| 成交笔数-笔    | int64   | -       |\n| 成交笔数-占总计  | float64 | 注意单位: % |\n"
  }, {
    "role" : "assistant",
    "tool_calls" : [ {
      "id" : "call_PYV0dDg1KTJUuZsg9qNnOhkx",
      "type" : "function",
      "function" : {
        "name" : "unified_api",
        "arguments" : "{\"api_name\":\"stock_szse_sector_summary\",\"params\":[\"symbol=当月\",\"date=202509\"]}"
      }
    } ]
  }, {
    "role" : "tool",
    "tool_call_id" : "call_PYV0dDg1KTJUuZsg9qNnOhkx",
    "content" : "[{\"项目名称\":\"合计\",\"成交股数-股数\":1102116260223,\"成交金额-人民币元\":12797346256776,\"成交笔数-占总计\":100.0,\"项目名称-英文\":\"Total\",\"成交金额-占总计\":100.0,\"成交股数-占总计\":100.0,\"成交笔数-笔\":1072706301,\"交易天数\":18},{\"项目名称\":\"农林牧渔\",\"成交股数-股数\":9399492707,\"成交金额-人民币元\":81959102700,\"成交笔数-占总计\":0.71,\"项目名称-英文\":\"Agriculture\",\"成交金额-占总计\":0.64,\"成交股数-占总计\":0.85,\"成交笔数-笔\":7661044,\"交易天数\":18},{\"项目名称\":\"采矿业\",\"成交股数-股数\":9878192740,\"成交金额-人民币元\":92263889071,\"成交笔数-占总计\":0.87,\"项目名称-英文\":\"Mining\",\"成交金额-占总计\":0.72,\"成交股数-占总计\":0.9,\"成交笔数-笔\":9355248,\"交易天数\":18},{\"项目名称\":\"制造业\",\"成交股数-股数\":658476162920,\"成交金额-人民币元\":8629767870655,\"成交笔数-占总计\":66.74,\"项目名称-英文\":\"Manufacturing\",\"成交金额-占总计\":67.43,\"成交股数-占总计\":59.75,\"成交笔数-笔\":715973899,\"交易天数\":18},{\"项目名称\":\"水电煤气\",\"成交股数-股数\":16229887048,\"成交金额-人民币元\":92119234528,\"成交笔数-占总计\":1.19,\"项目名称-英文\":\"Utilities\",\"成交金额-占总计\":0.72,\"成交股数-占总计\":1.47,\"成交笔数-笔\":12802444,\"交易天数\":18},{\"项目名称\":\"建筑业\",\"成交股数-股数\":15274277217,\"成交金额-人民币元\":79341083157,\"成交笔数-占总计\":0.95,\"项目名称-英文\":\"Construction\",\"成交金额-占总计\":0.62,\"成交股数-占总计\":1.39,\"成交笔数-笔\":10222345,\"交易天数\":18},{\"项目名称\":\"批发零售\",\"成交股数-股数\":52130678407,\"成交金额-人民币元\":446585758971,\"成交笔数-占总计\":3.79,\"项目名称-英文\":\"Wholesale & Retail\",\"成交金额-占总计\":3.49,\"成交股数-占总计\":4.73,\"成交笔数-笔\":40706994,\"交易天数\":18},{\"项目名称\":\"运输仓储\",\"成交股数-股数\":10863950008,\"成交金额-人民币元\":101119645056,\"成交笔数-占总计\":1.07,\"项目名称-英文\":\"Transportation\",\"成交金额-占总计\":0.79,\"成交股数-占总计\":0.99,\"成交笔数-笔\":11426446,\"交易天数\":18},{\"项目名称\":\"住宿餐饮\",\"成交股数-股数\":2114778738,\"成交金额-人民币元\":12977773203,\"成交笔数-占总计\":0.16,\"项目名称-英文\":\"Hotels & Catering\",\"成交金额-占总计\":0.1,\"成交股数-占总计\":0.19,\"成交笔数-笔\":1728478,\"交易天数\":18},{\"项目名称\":\"信息技术\",\"成交股数-股数\":129291809569,\"成交金额-人民币元\":1778343828765,\"成交笔数-占总计\":12.82,\"项目名称-英文\":\"IT\",\"成交金额-占总计\":13.9,\"成交股数-占总计\":11.73,\"成交笔数-笔\":137478307,\"交易天数\":18},{\"项目名称\":\"金融业\",\"成交股数-股数\":41461501835,\"成交金额-人民币元\":556014586952,\"成交笔数-占总计\":2.95,\"项目名称-英文\":\"Finance\",\"成交金额-占总计\":4.34,\"成交股数-占总计\":3.76,\"成交笔数-笔\":31633210,\"交易天数\":18},{\"项目名称\":\"房地产\",\"成交股数-股数\":31788728507,\"成交金额-人民币元\":130027421601,\"成交笔数-占总计\":1.29,\"项目名称-英文\":\"Real Estate\",\"成交金额-占总计\":1.02,\"成交股数-占总计\":2.88,\"成交笔数-笔\":13878880,\"交易天数\":18},{\"项目名称\":\"商务服务\",\"成交股数-股数\":71465288349,\"成交金额-人民币元\":403570860326,\"成交笔数-占总计\":3.57,\"项目名称-英文\":\"Business Support\",\"成交金额-占总计\":3.15,\"成交股数-占总计\":6.48,\"成交笔数-笔\":38310245,\"交易天数\":18},{\"项目名称\":\"科研服务\",\"成交股数-股数\":6923026099,\"成交金额-人民币元\":110626856303,\"成交笔数-占总计\":1.02,\"项目名称-英文\":\"Research & Development\",\"成交金额-占总计\":0.86,\"成交股数-占总计\":0.63,\"成交笔数-笔\":10972681,\"交易天数\":18},{\"项目名称\":\"公共环保\",\"成交股数-股数\":17729676291,\"成交金额-人民币元\":82012479434,\"成交笔数-占总计\":1.02,\"项目名称-英文\":\"Environmental Protection\",\"成交金额-占总计\":0.64,\"成交股数-占总计\":1.61,\"成交笔数-笔\":10919804,\"交易天数\":18},{\"项目名称\":\"居民服务\",\"成交股数-股数\":148411858,\"成交金额-人民币元\":1380962740,\"成交笔数-占总计\":0.02,\"项目名称-英文\":\"Resident Services\",\"成交金额-占总计\":0.01,\"成交股数-占总计\":0.01,\"成交笔数-笔\":217920,\"交易天数\":18},{\"项目名称\":\"教育\",\"成交股数-股数\":7073096176,\"成交金额-人民币元\":32550128302,\"成交笔数-占总计\":0.28,\"项目名称-英文\":\"Education\",\"成交金额-占总计\":0.25,\"成交股数-占总计\":0.64,\"成交笔数-笔\":3040949,\"交易天数\":18},{\"项目名称\":\"卫生\",\"成交股数-股数\":5628801958,\"成交金额-人民币元\":42095615991,\"成交笔数-占总计\":0.44,\"项目名称-英文\":\"Public Health\",\"成交金额-占总计\":0.33,\"成交股数-占总计\":0.51,\"成交笔数-笔\":4706600,\"交易天数\":18},{\"项目名称\":\"文化传播\",\"成交股数-股数\":14651178792,\"成交金额-人民币元\":107581655354,\"成交笔数-占总计\":0.93,\"项目名称-英文\":\"Media\",\"成交金额-占总计\":0.84,\"成交股数-占总计\":1.33,\"成交笔数-笔\":10017645,\"交易天数\":18},{\"项目名称\":\"综合\",\"成交股数-股数\":1587321004,\"成交金额-人民币元\":17007503667,\"成交笔数-占总计\":0.15,\"项目名称-英文\":\"Conglomerates\",\"成交金额-占总计\":0.13,\"成交股数-占总计\":0.14,\"成交笔数-笔\":1653162,\"交易天数\":18}]"
  } ],
  "stream" : false,
  "tools" : [ {
    "type" : "function",
    "function" : {
      "name" : "getStockBaseInfo",
      "description" : "根据股票代码，并返回给用户该支股票的基本面信息",
      "parameters" : {
        "type" : "object",
        "properties" : {
          "stockCode" : {
            "type" : "string",
            "description" : "股票代码"
          }
        },
        "required" : [ "stockCode" ]
      }
    }
  }, {
    "type" : "function",
    "function" : {
      "name" : "unified_api",
      "description" : "根据用户提供的信息，获取接口英文名称和参数（形如key=value），并返回特定的股票数据",
      "parameters" : {
        "type" : "object",
        "properties" : {
          "api_name" : {
            "type" : "string",
            "description" : "api_name"
          },
          "params" : {
            "type" : "array",
            "description" : "params",
            "items" : {
              "type" : "string"
            }
          }
        },
        "required" : [ "api_name", "params" ]
      }
    }
  } ]
}
~~~

#### Response
~~~
HTTP response:
- status code: 200
- headers: [Content-Type: text/html;charset=utf-8], [Date: Thu, 18 Sep 2025 22:32:29 GMT], [Server: Jetty(9.4.48.v20220622)], [Transfer-Encoding: chunked]
- body: <Body Content>
~~~

Body Content:
~~~json
{
  "id": "chatcmpl-CHHSvdd2YJVWp1L0c0XJJgNpFtZzL",
  "created": 1758234749,
  "model": "gpt-4o-mini-2024-07-18",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "以下是2025年9月份深圳证券交易所各行业的成交数据：\n\n\u003Ctable border=\"1\"\u003E\n    \u003Ctr\u003E\n        \u003Cth\u003E行业\u003C/th\u003E\n        \u003Cth\u003E成交股数 (股)\u003C/th\u003E\n        \u003Cth\u003E成交金额 (人民币元)\u003C/th\u003E\n        \u003Cth\u003E成交笔数 (笔)\u003C/th\u003E\n        \u003Cth\u003E交易天数\u003C/th\u003E\n        \u003Cth\u003E成交金额占总计 (%)\u003C/th\u003E\n        \u003Cth\u003E成交股数占总计 (%)\u003C/th\u003E\n        \u003Cth\u003E成交笔数占总计 (%)\u003C/th\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n        \u003Ctd\u003E合计\u003C/td\u003E\n        \u003Ctd\u003E1,102,116,262,023\u003C/td\u003E\n        \u003Ctd\u003E12,797,346,256,776\u003C/td\u003E\n        \u003Ctd\u003E1,072,706,301\u003C/td\u003E\n        \u003Ctd\u003E18\u003C/td\u003E\n        \u003Ctd\u003E100.00\u003C/td\u003E\n        \u003Ctd\u003E100.00\u003C/td\u003E\n        \u003Ctd\u003E100.00\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n        \u003Ctd\u003E农林牧渔\u003C/td\u003E\n        \u003Ctd\u003E9,399,492,707\u003C/td\u003E\n        \u003Ctd\u003E81,959,102,700\u003C/td\u003E\n        \u003Ctd\u003E7,661,044\u003C/td\u003E\n        \u003Ctd\u003E18\u003C/td\u003E\n        \u003Ctd\u003E0.64\u003C/td\u003E\n        \u003Ctd\u003E0.85\u003C/td\u003E\n        \u003Ctd\u003E0.71\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n        \u003Ctd\u003E采矿业\u003C/td\u003E\n        \u003Ctd\u003E9,878,192,740\u003C/td\u003E\n        \u003Ctd\u003E92,263,889,071\u003C/td\u003E\n        \u003Ctd\u003E9,355,248\u003C/td\u003E\n        \u003Ctd\u003E18\u003C/td\u003E\n        \u003Ctd\u003E0.72\u003C/td\u003E\n        \u003Ctd\u003E0.90\u003C/td\u003E\n        \u003Ctd\u003E0.87\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n        \u003Ctd\u003E制造业\u003C/td\u003E\n        \u003Ctd\u003E658,476,162,920\u003C/td\u003E\n        \u003Ctd\u003E8,629,767,870,655\u003C/td\u003E\n        \u003Ctd\u003E715,973,899\u003C/td\u003E\n        \u003Ctd\u003E18\u003C/td\u003E\n        \u003Ctd\u003E67.43\u003C/td\u003E\n        \u003Ctd\u003E59.75\u003C/td\u003E\n        \u003Ctd\u003E66.74\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n        \u003Ctd\u003E水电煤气\u003C/td\u003E\n        \u003Ctd\u003E16,229,887,048\u003C/td\u003E\n        \u003Ctd\u003E92,119,234,528\u003C/td\u003E\n        \u003Ctd\u003E12,802,444\u003C/td\u003E\n        \u003Ctd\u003E18\u003C/td\u003E\n        \u003Ctd\u003E0.72\u003C/td\u003E\n        \u003Ctd\u003E1.47\u003C/td\u003E\n        \u003Ctd\u003E1.19\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n        \u003Ctd\u003E建筑业\u003C/td\u003E\n        \u003Ctd\u003E15,274,277,217\u003C/td\u003E\n        \u003Ctd\u003E79,341,083,157\u003C/td\u003E\n        \u003Ctd\u003E10,222,345\u003C/td\u003E\n        \u003Ctd\u003E18\u003C/td\u003E\n        \u003Ctd\u003E0.62\u003C/td\u003E\n        \u003Ctd\u003E1.39\u003C/td\u003E\n        \u003Ctd\u003E0.95\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n        \u003Ctd\u003E批发零售\u003C/td\u003E\n        \u003Ctd\u003E52,130,678,407\u003C/td\u003E\n        \u003Ctd\u003E446,585,758,971\u003C/td\u003E\n        \u003Ctd\u003E40,706,994\u003C/td\u003E\n        \u003Ctd\u003E18\u003C/td\u003E\n        \u003Ctd\u003E3.49\u003C/td\u003E\n        \u003Ctd\u003E4.73\u003C/td\u003E\n        \u003Ctd\u003E3.79\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n        \u003Ctd\u003E运输仓储\u003C/td\u003E\n        \u003Ctd\u003E10,863,950,008\u003C/td\u003E\n        \u003Ctd\u003E101,119,645,056\u003C/td\u003E\n        \u003Ctd\u003E11,426,446\u003C/td\u003E\n        \u003Ctd\u003E18\u003C/td\u003E\n        \u003Ctd\u003E0.79\u003C/td\u003E\n        \u003Ctd\u003E0.99\u003C/td\u003E\n        \u003Ctd\u003E1.07\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n        \u003Ctd\u003E住宿餐饮\u003C/td\u003E\n        \u003Ctd\u003E2,114,778,738\u003C/td\u003E\n        \u003Ctd\u003E12,977,773,203\u003C/td\u003E\n        \u003Ctd\u003E1,728,478\u003C/td\u003E\n        \u003Ctd\u003E18\u003C/td\u003E\n        \u003Ctd\u003E0.10\u003C/td\u003E\n        \u003Ctd\u003E0.19\u003C/td\u003E\n        \u003Ctd\u003E0.16\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n        \u003Ctd\u003E信息技术\u003C/td\u003E\n        \u003Ctd\u003E129,291,809,569\u003C/td\u003E\n        \u003Ctd\u003E1,778,343,828,765\u003C/td\u003E\n        \u003Ctd\u003E137,478,307\u003C/td\u003E\n        \u003Ctd\u003E18\u003C/td\u003E\n        \u003Ctd\u003E13.90\u003C/td\u003E\n        \u003Ctd\u003E11.73\u003C/td\u003E\n        \u003Ctd\u003E12.82\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n        \u003Ctd\u003E金融业\u003C/td\u003E\n        \u003Ctd\u003E41,461,501,835\u003C/td\u003E\n        \u003Ctd\u003E556,014,586,952\u003C/td\u003E\n        \u003Ctd\u003E31,633,210\u003C/td\u003E\n        \u003Ctd\u003E18\u003C/td\u003E\n        \u003Ctd\u003E4.34\u003C/td\u003E\n        \u003Ctd\u003E3.76\u003C/td\u003E\n        \u003Ctd\u003E2.95\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n        \u003Ctd\u003E房地产\u003C/td\u003E\n        \u003Ctd\u003E31,788,728,507\u003C/td\u003E\n        \u003Ctd\u003E130,027,421,601\u003C/td\u003E\n        \u003Ctd\u003E13,878,880\u003C/td\u003E\n        \u003Ctd\u003E18\u003C/td\u003E\n        \u003Ctd\u003E1.02\u003C/td\u003E\n        \u003Ctd\u003E2.88\u003C/td\u003E\n        \u003Ctd\u003E1.29\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n        \u003Ctd\u003E商务服务\u003C/td\u003E\n        \u003Ctd\u003E71,465,288,349\u003C/td\u003E\n        \u003Ctd\u003E403,570,860,326\u003C/td\u003E\n        \u003Ctd\u003E38,310,245\u003C/td\u003E\n        \u003Ctd\u003E18\u003C/td\u003E\n        \u003Ctd\u003E3.15\u003C/td\u003E\n        \u003Ctd\u003E6.48\u003C/td\u003E\n        \u003Ctd\u003E3.57\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n        \u003Ctd\u003E科研服务\u003C/td\u003E\n        \u003Ctd\u003E6,923,026,099\u003C/td\u003E\n        \u003Ctd\u003E110,626,856,303\u003C/td\u003E\n        \u003Ctd\u003E10,972,681\u003C/td\u003E\n        \u003Ctd\u003E18\u003C/td\u003E\n        \u003Ctd\u003E0.86\u003C/td\u003E\n        \u003Ctd\u003E0.63\u003C/td\u003E\n        \u003Ctd\u003E1.02\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n        \u003Ctd\u003E公共环保\u003C/td\u003E\n        \u003Ctd\u003E17,729,676,291\u003C/td\u003E\n        \u003Ctd\u003E82,012,479,434\u003C/td\u003E\n        \u003Ctd\u003E10,919,804\u003C/td\u003E\n        \u003Ctd\u003E18\u003C/td\u003E\n        \u003Ctd\u003E0.64\u003C/td\u003E\n        \u003Ctd\u003E1.61\u003C/td\u003E\n        \u003Ctd\u003E1.02\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n        \u003Ctd\u003E居民服务\u003C/td\u003E\n        \u003Ctd\u003E148,411,858\u003C/td\u003E\n        \u003Ctd\u003E1,380,962,740\u003C/td\u003E\n        \u003Ctd\u003E217,920\u003C/td\u003E\n        \u003Ctd\u003E18\u003C/td\u003E\n        \u003Ctd\u003E0.01\u003C/td\u003E\n        \u003Ctd\u003E0.01\u003C/td\u003E\n        \u003Ctd\u003E0.02\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n        \u003Ctd\u003E教育\u003C/td\u003E\n        \u003Ctd\u003E7,073,096,176\u003C/td\u003E\n        \u003Ctd\u003E32,550,128,302\u003C/td\u003E\n        \u003Ctd\u003E3,040,949\u003C/td\u003E\n        \u003Ctd\u003E18\u003C/td\u003E\n        \u003Ctd\u003E0.25\u003C/td\u003E\n        \u003Ctd\u003E0.64\u003C/td\u003E\n        \u003Ctd\u003E0.28\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n        \u003Ctd\u003E卫生\u003C/td\u003E\n        \u003Ctd\u003E5,628,801,958\u003C/td\u003E\n        \u003Ctd\u003E42,095,615,991\u003C/td\u003E\n        \u003Ctd\u003E4,706,600\u003C/td\u003E\n        \u003Ctd\u003E18\u003C/td\u003E\n        \u003Ctd\u003E0.33\u003C/td\u003E\n        \u003Ctd\u003E0.51\u003C/td\u003E\n        \u003Ctd\u003E0.44\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n        \u003Ctd\u003E文化传播\u003C/td\u003E\n        \u003Ctd\u003E14,651,178,792\u003C/td\u003E\n        \u003Ctd\u003E107,581,655,354\u003C/td\u003E\n        \u003Ctd\u003E10,017,645\u003C/td\u003E\n        \u003Ctd\u003E18\u003C/td\u003E\n        \u003Ctd\u003E0.84\u003C/td\u003E\n        \u003Ctd\u003E1.33\u003C/td\u003E\n        \u003Ctd\u003E0.93\u003C/td\u003E\n    \u003C/tr\u003E\n    \u003Ctr\u003E\n        \u003Ctd\u003E综合\u003C/td\u003E\n        \u003Ctd\u003E1,587,321,004\u003C/td\u003E\n        \u003Ctd\u003E17,007,503,667\u003C/td\u003E\n        \u003Ctd\u003E1,653,162\u003C/td\u003E\n        \u003Ctd\u003E18\u003C/td\u003E\n        \u003Ctd\u003E0.13\u003C/td\u003E\n        \u003Ctd\u003E0.14\u003C/td\u003E\n        \u003Ctd\u003E0.15\u003C/td\u003E\n    \u003C/tr\u003E\n\u003C/table\u003E\n\n如果您需要更详细的信息或有其他问题，请随时告诉我！"
      },
      "finish_reason": "stop"
    }
  ],
  "usage": {
    "prompt_tokens": 2465,
    "completion_tokens": 2057,
    "total_tokens": 4522
  },
  "system_fingerprint": "fp_560af6e559"
}
~~~

## Chains and Agents
### 链（Chains）
通常，一个应用程序需要按特定顺序调用多个组件。在 LangChain 中，被称为链（Chain）。链简化了开发更复杂应用程序的过程，并使调试、维护和改进更加容易。
链还可以组合多个链来构建更复杂的应用程序，这些应用程序可能需要与多个语言模型交互。

### 代理（Agents）
LangChain 还提供了更强大的结构，例如代理（Agent）。与链不同，代理将语言模型用作推理引擎，以确定应该采取哪些操作以及操作的顺序。我们还可以为代理提供访问合适工具的权限，以执行必要的操作。
在 LangChain4j 中，代理作为 AI 服务（AI Services）提供，用于声明性地定义复杂的 AI 行为。让我们看看如何通过提供一个计算器工具，为 AI 服务赋能，从而使语言模型能够执行计算。

## 知识库与RAG
### RAG基本逻辑
创建AI服务时，可指定一个 RetrievalAugmentor 实例。
<font color=red>**重点：**每次调用AI服务时，指定的 RetrievalAugmentor 将被调用来增强当前的 UserMessage（用户消息）。</font>
注：目前没有显示指定RetrievalAugmentor。

StockAgent.java
~~~java
@AiService(
        wiringMode = EXPLICIT
        , chatModel = "openAiChatModel"
        , chatMemoryProvider = "chatMemoryProviderStock"
        , tools = "akhareTools"
        , contentRetriever = "contentRetrieverStockPincone" //配置向量存储
)
public interface StockAgent {
    @SystemMessage(fromResource = "system-prompt-template.txt")
    String chat(@MemoryId Long memoryId, @UserMessage String userMessage);
}
~~~
StockAgentConfig.java
~~~java
@Bean
    ContentRetriever contentRetrieverStockPincone() {
        // 创建一个 EmbeddingStoreContentRetriever 对象，用于从嵌入存储中索引内容
        return EmbeddingStoreContentRetriever
                .builder()
                // 设置用于生成嵌入向量的嵌入模型
                .embeddingModel(qwen3EmbeddingModel)
                // 指定要使用的嵌入存储
                .embeddingStore(embeddingStore)
                // 设置最大的索引结果数量，这里表示最多返回 1 条匹配结果
                .maxResults(1)
                // 设置最小得分阈（yu）值。只有得分大于等于 0.8 的结果才能被返回
                .minScore(0.8)
                // 构建最终的 EmbeddingStoreContentRetriever 实例
                .build();
    }
~~~

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


## 参考资料
- [Atlas Vector Search与 LangChain4j 集成](https://www.mongodb.com/zh-cn/docs/atlas/ai-integrations/langchain4j/)
- https://docs.langchain4j.dev/category/tutorials
- [**RAG技术全面解析：Langchain4j...**](https://cloud.tencent.com/developer/article/2453301)
- 