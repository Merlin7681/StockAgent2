package com.merlin.langchain.utility;

import dev.langchain4j.http.client.HttpClient;
import dev.langchain4j.http.client.HttpMethod;
import dev.langchain4j.http.client.HttpRequest;
import dev.langchain4j.http.client.spring.restclient.SpringRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.http.client.HttpClient;
import dev.langchain4j.http.client.HttpRequest;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Collections;

@Component
public class WebTools {
    Logger logger = LoggerFactory.getLogger(WebTools.class);

    private final SpringRestClient httpClient = SpringRestClient.builder()
            .connectTimeout(Duration.ofSeconds(10))
            .readTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * 获取指定URL的网页内容。比如：请访问https://langchain4j.dev/，并简要介绍该框架的核心特性
     * @param url
     * @return 网页内容
     */
    public String fetchWebContent(String url) {
        try {
            logger.debug("开始获取网页内容...");
            // 使用 SpringRestClient 发送 GET 请求
            HttpRequest.Builder requestBuilder = HttpRequest.builder();
            HttpRequest request = requestBuilder
                    .url(url)
                    .method(HttpMethod.GET)
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .build();
            String response = httpClient.execute(request).body();


            logger.debug("获取网页内容成功...");
            // 内容过长时截断，避免超出模型上下文限制
            return response.length() > 3000 ? response.substring(0, 3000) + "...[内容已截断]" : response;
        } catch (Exception e) {
            logger.error("获取网页内容出错: ", e);
            return "获取网页内容出错: " + e.getMessage();
        }
    }
}
