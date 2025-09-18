package com.merlin.langchain.utility;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONException;

public class JsonParserUtil {

    /**
     * 判断JSON字符串是否为数组
     * @param jsonStr JSON字符串
     * @return 是否为JSON数组
     */
    public static boolean isJsonArray(String jsonStr) {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return false;
        }
        // 去除首尾空格
        String trimmedStr = jsonStr.trim();
        // JSON数组以[开头并以]结尾
        return trimmedStr.startsWith("[") && trimmedStr.endsWith("]");
    }

    /**
     * 解析JSON字符串
     * @param jsonStr JSON字符串
     */
    public static void parseJson(String jsonStr) {
        if (isJsonArray(jsonStr)) {
            try {
                // 解析为JSON数组
                JSONArray jsonArray = JSONArray.parseArray(jsonStr);
                System.out.println("成功解析为JSON数组，包含 " + jsonArray.size() + " 个元素");

                // 遍历数组示例
                if (jsonArray.size() > 0) {
                    JSONObject firstItem = jsonArray.getJSONObject(0);
                    System.out.println("数组第一个元素：" + firstItem.toJSONString());
                }
            } catch (JSONException e) {
                System.err.println("解析JSON数组失败：" + e.getMessage());
            }
        } else {
            try {
                // 解析为JSON对象
                JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                System.out.println("成功解析为JSON对象，包含 " + jsonObject.size() + " 个键值对");

                // 输出所有键示例
                System.out.println("对象包含的键：" + jsonObject.keySet());
            } catch (JSONException e) {
                System.err.println("解析JSON对象失败：" + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        // 测试JSON数组
        String arrayJson = "[{\"name\":\"张三\",\"age\":20},{\"name\":\"李四\",\"age\":25}]";
        System.out.println("测试JSON数组：");
        parseJson(arrayJson);

        // 测试JSON对象
        String objectJson = "{\"total\":2,\"data\":[{\"name\":\"张三\"},{\"name\":\"李四\"}]}";
        System.out.println("\n测试JSON对象：");
        parseJson(objectJson);

        // 测试无效JSON
        String invalidJson = "{\"name\":\"张三\", age:20";
        System.out.println("\n测试无效JSON：");
        parseJson(invalidJson);
    }
}
