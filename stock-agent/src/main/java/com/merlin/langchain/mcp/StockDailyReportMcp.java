package com.merlin.langchain.mcp;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@EnableScheduling // 开启Spring定时调度（MCP核心开关）
public class StockDailyReportMcp {

    // 1. 初始化LangChain4j大模型客户端（绑定大模型）
    @Autowired
    private OpenAiChatModel openAiChatModel;

    // 2. MCP定时触发器：每天9点执行（cron表达式：秒 分 时 日 月 周）
    @Scheduled(cron = "0 0 9 * * ?")
    public void executeDailyStockTask() {
        try {
            System.out.println("MCP触发每日股票任务：" + System.currentTimeMillis());

            // 步骤1：抓取股票核心数据（模拟，实际替换为真实接口/数据库查询）
            String stockData = getStockCoreData();

            // 步骤2：调用大模型分析数据（LangChain4j核心逻辑，MCP触发的核心任务）
            String dailyReport = callLlmGenerateReport(stockData);

            // 步骤3：后续处理（如保存日报到文件/数据库，MCP任务收尾）
            saveReport(dailyReport);

        } catch (Exception e) {
            System.err.println("MCP任务执行失败：" + e.getMessage());
        }
    }

    // 模拟：抓取并筛选股票核心数据（对应之前提到的“信息瘦身”）
    private String getStockCoreData() {
        Map<String, Object> coreData = new HashMap<>();
        coreData.put("股票代码", "600036");
        coreData.put("日期", "2024-05-20");
        coreData.put("开盘价", "12.56");
        coreData.put("收盘价", "12.88");
        coreData.put("成交量", "1200万手");
        coreData.put("当日公告", "拟回购5亿元公司股份");
        return coreData.toString(); // 结构化数据，降低大模型理解成本
    }

    // 核心：用LangChain4j调用大模型生成日报
    private String callLlmGenerateReport(String stockData) {
        // 构建大模型提示词（结合瘦身后的核心数据）
        String prompt = "基于以下股票核心数据，生成100字以内的日报，包含价格波动、关键事件和简短结论：" + stockData;

        // LangChain4j调用大模型并返回结果
        return openAiChatModel.chat(prompt);
    }

    // 模拟：保存大模型生成的日报
    private void saveReport(String report) {
        System.out.println("大模型生成日报：" + report);
        // 实际可写文件、存数据库等
    }
}
