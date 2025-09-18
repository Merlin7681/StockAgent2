package com.merlin.langchain;

import com.merlin.langchain.utility.UrlChineseEncoder;
import org.junit.jupiter.api.Test;

public class UrlChineseEncoderTest {
    @Test
    public void test() {
        String originalUrl = "http://localhost:8080/api/public/stock_szse_sector_summary?symbol=当月&date=202509";
        String encodedUrl = UrlChineseEncoder.encodeChineseInUrl(originalUrl);
        System.out.println("编码后URL: " + encodedUrl);
        // 输出：http://localhost:8080/api/public/stock_szse_sector_summary?symbol=%E5%BD%93%E6%9C%88&date=202509
    }

}
