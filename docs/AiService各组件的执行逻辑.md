

~~~java
@AiService(
        wiringMode = EXPLICIT
        , chatModel = "openAiChatModel"
        , chatMemoryProvider = "chatMemoryProviderStock"
        , tools = "akhareTools"
        // , retrievalAugmentor = "retrievalAugmentor" 检索增强器
        , contentRetriever = "contentRetrieverStockPincone" //配置向量存储
)
public interface StockAgent {
    @SystemMessage(fromResource = "system-prompt-template.txt")
    String chat(@MemoryId Long memoryId, @UserMessage String userMessage);

    @SystemMessage("你是我的好朋友，请用东北话回答问题。")
    String chat2(@MemoryId Long memoryId, @UserMessage String userMessage);
}
~~~

在 `@AiService` 注解配置的 `StockAgent` 中，各组件的执行逻辑可从**核心流程**和**组件协作**两方面理解：

### 一、核心流程：“用户请求 → 工具/检索增强 → LLM 推理 → 生成响应”
当调用 `StockAgent` 的 `chat` 或 `chat2` 方法时，整体流程如下：
1. **上下文加载**：通过 `@MemoryId` 指定的 `memoryId`，从 `chatMemoryProvider`（这里是 `chatMemoryProviderStock`）中加载历史对话上下文，确保多轮对话的连续性。
2. **系统提示注入**：
    - `chat` 方法：从 `system-prompt-template.txt` 资源文件中读取**系统提示词**（比如定义 Agent 的角色、任务规则等）。
    - `chat2` 方法：直接注入硬编码的系统提示 `“你是我的好朋友，请用东北话回答问题。”`，强制 LLM 用东北话风格响应。
3. **用户输入整合**：将 `@UserMessage` 标注的用户输入，与加载的上下文、系统提示拼接成完整的 Prompt。
4. **检索增强（若启用）**：
    - 通过 `contentRetriever`（这里是 `contentRetrieverStockPinecone`，基于 Pinecone 向量库），根据用户问题从外部知识库（如文档向量库）中检索相关知识片段，补充到 Prompt 中，提升 LLM 回答的准确性（类似“给 LLM 额外的参考资料”）。
    - 图中 `retrievalAugmentor` 被注释，若启用，它会更精细地管理“检索 → 内容增强 → 传给 LLM”的流程。
5. **工具调用（若触发）**：
    - `tools` 指定为 `akhareTools`，当 LLM 推理时判断需要调用外部工具（比如查询实时股票数据、执行计算等），会触发对应的工具方法，工具返回的结果会再次反馈给 LLM，由 LLM 整合结果生成最终响应。
6. **LLM 推理**：`chatModel` 指定为 `openAiChatModel`（调用 OpenAI 的 ChatCompletion API），基于整合后的 Prompt（含上下文、系统提示、检索内容、工具结果等）进行推理，生成自然语言响应。
7. **响应返回与记忆更新**：将 LLM 生成的响应返回给调用方，并将本次对话（用户问题 + 系统响应）更新到 `chatMemoryProvider` 中，供后续对话复用。


### 二、各组件的角色与协作细节
- **`wiringMode = EXPLICIT`**：表示“显式 wiring”，即组件间的依赖需要通过配置（而非自动扫描）明确指定，确保开发者能精确控制每个组件的绑定。
- **`chatModel = "openAiChatModel"`**：负责与大语言模型（LLM）交互，是“推理核心”，所有文本生成、工具调用决策都由它驱动。
- **`chatMemoryProvider = "chatMemoryProviderStock"`**：管理对话记忆，实现“多轮对话上下文维持”，让 Agent 能“记住”之前的交流内容。
- **`tools = "akhareTools"`**：提供外部工具能力，当 LLM 判断问题需要工具辅助（如“查某只股票的实时价格”），会调用这里配置的工具集，拓展 Agent 的能力边界（从“纯文本推理”到“工具增强的智能”）。
- **`contentRetriever = "contentRetrieverStockPinecone"`**：对接 Pinecone 向量数据库，实现“检索增强生成（RAG）”，让 Agent 能基于外部知识库回答问题，解决 LLM 自身“知识 cutoff”的问题。
- **`@SystemMessage`**：为 LLM 注入“角色设定”或“任务规则”，是引导 LLM 行为的关键（比如 `chat2` 强制东北话风格）。


简言之，这些组件围绕 **“LLM 为核心，记忆保上下文，工具拓能力，检索增知识”** 的逻辑协作，让 `StockAgent` 能像“有记忆、有工具、有外部知识的智能助手”一样工作～