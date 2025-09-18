package com.merlin.langchain.utility;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlChineseEncoder {

    /**
     * 自动编码URL中的中文字符（仅编码参数值）
     * @param originalUrl 原始包含中文的URL
     * @return 编码后的URL
     */
    public static String encodeChineseInUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.isEmpty()) {
            return originalUrl;
        }

        // 正则匹配URL中的参数键值对（如key=value）
        Pattern pattern = Pattern.compile("([?&])([^=]+)=([^&]+)");
        Matcher matcher = pattern.matcher(originalUrl);
        StringBuffer encodedUrl = new StringBuffer();

        try {
            while (matcher.find()) {
                String separator = matcher.group(1); // ? 或 &
                String key = matcher.group(2);       // 参数名
                String value = matcher.group(3);     // 参数值（可能包含中文）

                // 仅编码参数值
                String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8.name());
                // 拼接编码后的键值对
                matcher.appendReplacement(encodedUrl, separator + key + "=" + encodedValue);
            }
            matcher.appendTail(encodedUrl);
        } catch (Exception e) {
            // 编码失败时返回原始URL
            e.printStackTrace();
            return originalUrl;
        }

        return encodedUrl.toString();
    }

}
