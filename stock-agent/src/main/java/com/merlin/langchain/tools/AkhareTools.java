package com.merlin.langchain.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.merlin.langchain.utility.JsonParserUtil;
import com.merlin.langchain.utility.RAGTool;
import com.merlin.langchain.utility.WebTools;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Agent 工具类
 * NOTE：只有这个类（及里面的@Tools）注册到了AI Service中，才会被AI Service调用
 * WARNING：不要注册过多的Tools，否则会耗尽AI Service的资源token
 */
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
    @Tool(name = "unified_api", value = "根据用户提供的信息，获取接口英文名称和参数（形如key=value），并返回特定的股票数据")
    public String unifiedInterface(@P(value = "api_name") String api_name, @P(value = "params") String[] params) {
        AkhareTools.log.debug("开始执行 unifiedInterface: \n api_name:{}\n params: {}"
                , api_name, java.util.Arrays.toString(params));

        // 如果info中包含中文字符，则返回接口方法分析错误 并返回
        if (api_name.matches(".*[\u4e00-\u9fa5]+.*")) {
            return "接口方法分析错误:接口方法解析错误";
        }

        String curUrl = api_name;
        if (params != null && params.length > 0) {
            AkhareTools.log.debug("开始执行 unifiedInterface: params != null && params.length > 0");
            curUrl = curUrl + "?" + params[0];
        }
        AkhareTools.log.debug("开始执行 unifiedInterface: curUrl:-{}-", curUrl);
        String result  = this.getAkShareMethod(curUrl);
        log.debug("开始执行 unifiedInterface: result:-{}-", result);

        if (result == null || result.isEmpty() || result.contains("Internal Server Error")) {
            return "接口返回错误:请检查远程服务及URL地址是否正确";
        }
        try {
            // 判断是否是json数组
            if(JsonParserUtil.isJsonArray(result)) {
                JSONArray jsonArray = JSONArray.parseArray(result);
                // 获取前20个元素，如果元素数量不足20个，就取全部
                JSONArray subArray = new JSONArray(jsonArray.subList(0, Math.min(jsonArray.size(), 20)));
                String tmp = subArray.toJSONString();
                AkhareTools.log.debug("JSONArray Result:{}-", tmp);
                return tmp;
            } else {
                // WARN：将result转为json对象，并只保留前20个数值
                JSONObject jsonObject = JSONObject.parseObject(result);
                jsonObject.put("result", jsonObject.getJSONArray("result").subList(0, Math.min(jsonObject.getJSONArray("result").size(), 20)));
                String tmp = jsonObject.toJSONString();
                AkhareTools.log.debug("JSONObject Result:{}-", tmp);
                return tmp;
            }
        } catch(Exception e) {
            log.error("解析 unifiedInterface 返回结果时发生异常: ", e);
            return result.substring(0, Math.min(result.length(), 100));
        }

    }

    @Autowired
    private WebTools webTools;

    @Tool(value = "获取指定URL的网页内容，参数为完整的URL地址")
    public String fetchWebContent(@P(value = "url") String url) {
        log.debug("fetchWebContent:{}-", url);
        return webTools.fetchWebContent(url);
    }

}
