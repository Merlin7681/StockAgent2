package com.merlin.langchain.tools;

import com.merlin.langchain.utility.UrlChineseEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AkShareCommon {
    protected static String AKTOOLS_API_BASE_URL = "http://localhost:8080/api/public/";
    private static final Logger log = LoggerFactory.getLogger(AkShareCommon.class);

    /**
     * 此函数可用于调用所有参数仅为stockCode的AkShare接口
     * @param method
     * @param stockCode
     * @return
     */
    public String getAkShare4stockCode(String method, String stockCode) {
        String result = "";
        try {
            // 构建请求参数
            String encodedStockCode = URLEncoder.encode(stockCode, StandardCharsets.UTF_8.toString());
            String suffixUrl = method + "?symbol=" + encodedStockCode;
            result = getAkShareMethod(suffixUrl);

        } catch (Exception e) {
            log.error("Exception occurred in getAkShaareMethod", e);

            result = "Exception: " + e.getMessage();
        }
        
        return result;
    }

    protected String getAkShareMethod(String suffixUrl) {
        String result = "";
        try {
            // 构建请求参数
            String requestUrl = AKTOOLS_API_BASE_URL + suffixUrl;
            log.debug("requestUrl(raw):{}-", requestUrl);
            // 对url进行编码，避免因包含中文字符而导致请求错误
            requestUrl = UrlChineseEncoder.encodeChineseInUrl(requestUrl);
            log.debug("requestUrl(encoded):{}-", requestUrl);

            // 创建 HTTP 连接
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // 读取响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                result = response.toString();
            } else {
                result = "Error: " + responseCode;
            }

            // 断开连接
            connection.disconnect();

        } catch (Exception e) {
            log.error("Exception occurred in getAkShaareMethod", e);

            result = "Exception: " + e.getMessage();
        }

        return result;
    }
    
    /**
     * 通过 AKTools 服务访问 akshare 的 stock_individual_info_em 方法，获取个股信息
     * @param stockCode 股票代码（六位），形如603777
     * @return 个股信息的 JSON 字符串
     */
    public String getStockIndividualInfo(String stockCode) {
        String method = "stock_individual_info_em";
        return getAkShare4stockCode(method, getShortStockCode(stockCode));
        
    }

    /**
     * 通过 AKTools 服务访问 akshare 的 stock_financial_abstract 方法，获取个股财务摘要信息
     * 并只保留current年的数据
     * @param stockCode 股票代码（六位），形如603777
     * @return current年个股财务摘要信息的 JSON 字符串
     */
    public String getStockFinancialAbstract(String stockCode) {
        // 获取当前年份
        int currentYear = java.time.Year.now().getValue();
        String prefix = String.valueOf(currentYear);

        String method = "stock_financial_abstract";
        String result = getAkShare4stockCode(method, getShortStockCode(stockCode));
        
        // 检查结果是否为空或包含错误信息
        if (result == null || result.isEmpty() || result.startsWith("Error") || result.startsWith("Exception")) {
            return result;
        }
        
        try {
            // 使用手动解析的方式筛选2025年数据
            // 简单的JSON解析逻辑，针对已知的格式
            StringBuilder filteredResult = new StringBuilder();
            filteredResult.append("[");
            
            // 提取JSON数组中的每个对象
            Pattern arrayItemPattern = Pattern.compile("\\{(.*?)\\}", Pattern.DOTALL);
            Matcher arrayItemMatcher = arrayItemPattern.matcher(result);
            
            boolean firstItem = true;
            
            while (arrayItemMatcher.find()) {
                String jsonItem = arrayItemMatcher.group(0);
                
                // 创建筛选后的对象字符串
                StringBuilder filteredItem = new StringBuilder();
                filteredItem.append("{");
                
                // 提取键值对
                Pattern keyValuePattern = Pattern.compile("([\"\\'])(.*?)\\1:(.*?)(,|$)", Pattern.DOTALL);
                Matcher keyValueMatcher = keyValuePattern.matcher(jsonItem);
                
                boolean firstKeyValue = true;
                boolean has2025Data = false;
                
                while (keyValueMatcher.find()) {
                    String quote = keyValueMatcher.group(1);
                    String key = keyValueMatcher.group(2);
                    String value = keyValueMatcher.group(3);
                    String separator = keyValueMatcher.group(4);
                    
                    // 检查是否包含2025年的数据
                    if (key.startsWith(prefix)) {
                        has2025Data = true;
                    }
                    
                    // 只保留非年份字段或2025年的字段
                    if (!key.matches("\\d{8}") || key.startsWith(prefix)) {
                        if (!firstKeyValue) {
                            filteredItem.append(",");
                        }
                        filteredItem.append(quote).append(key).append(quote).append(":").append(value);
                        firstKeyValue = false;
                    }
                }
                
                filteredItem.append("}");
                
                // 只有包含2025年数据的条目才添加到结果中
                if (has2025Data) {
                    if (!firstItem) {
                        filteredResult.append(",");
                    }
                    filteredResult.append(filteredItem);
                    firstItem = false;
                }
            }
            
            filteredResult.append("]");
            
            // 返回筛选后的JSON字符串
            return filteredResult.toString();
            
        } catch (Exception e) {
            log.error("Exception occurred while filtering financial data", e);
            return "Exception during filtering: " + e.getMessage();
        }
    }


    /**
     * 返回6位格式的股票代码
     * @param stockCode
     * @return
     */
    private String getShortStockCode(String stockCode){
        // 如果stockCode为空或小于6位，则前方补0
        if(stockCode == null || stockCode.length() < 6){
            stockCode = String.format("%06d", Integer.parseInt(stockCode));
            return stockCode;
        }
        // 如果stockCode=6位，则直接返回；否则返回stockCode后6位
        if(stockCode.length() == 6){
            return stockCode;
        }
        return stockCode.substring(stockCode.length() - 6);

    }
    
}
