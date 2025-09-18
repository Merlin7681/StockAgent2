package com.merlin.langchain.tools;

import com.alibaba.fastjson.JSONObject;
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
    private RAGTool ragTool;


    @Tool(value = "根据股票代码，并返回给用户该支股票的基本面信息")
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
     * @param params 包含API元数据和文本内容的文本片段，用于进一步处理
     * @return 处理结果字符串
     */
    @Tool(name = "unified_api", value = "根据用户提供的信息，获取接口英文名称和参数，并返回特定的股票数据")
    public String unifiedInterface(@P(value = "info") String info, @P(value = "params") String[] params) {
        AkhareTools.log.debug("开始执行 unifiedInterface: \n info:{}\n params: {}"
                , info, java.util.Arrays.toString(params));

        // 如果info中包含中文字符，则返回接口方法分析错误 并返回
        if (info.matches(".*[\u4e00-\u9fa5]+.*")) {
            return "接口方法分析错误:接口方法解析错误";
        }

        String curUrl = info;
        if (params != null && params.length > 0) {
            AkhareTools.log.debug("开始执行 unifiedInterface: params != null && params.length > 0");
            curUrl = curUrl + "?" + params[0];
        }
        AkhareTools.log.debug("开始执行 unifiedInterface: curUrl:-{}-", curUrl);
        String result  = this.getAkShareMethod(curUrl);

        try {
            // WARN：将result转为json对象，并只保留前20个数值
            JSONObject jsonObject = JSONObject.parseObject(result);
            jsonObject.put("result", jsonObject.getJSONArray("result").subList(0, Math.min(jsonObject.getJSONArray("result").size(), 20)));
            AkhareTools.log.debug("开始执行 unifiedInterface: Over!");
            return jsonObject.toJSONString();
        } catch(Exception e) {
            log.error("解析 unifiedInterface 返回结果时发生异常: ", e);
            return result.substring(0, Math.min(result.length(), 100));
        }

    }

}
