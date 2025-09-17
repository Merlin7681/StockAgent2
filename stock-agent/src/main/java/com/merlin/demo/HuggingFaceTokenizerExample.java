package com.merlin.demo;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;


class HuggingFaceTokenizerExample {
    public static void main(String[] args) {
        try {
            // 1. 调用静态方法加载预训练分词器（例如BERT的基础分词器）
            // 支持的名称可参考HuggingFace Hub上的模型库（https://huggingface.co/models）
            HuggingFaceTokenizer tokenizer = HuggingFaceTokenizer.newInstance("bert-base-uncased");

            // 2. 准备要处理的文本
            String text = "Hello, Hugging Face! This is a test sentence.";
            System.out.println("原始文本: " + text);


        } catch (RuntimeException e) {
            // 处理加载失败的情况（如网络问题、名称错误等）
            System.err.println("加载分词器失败: " + e.getMessage());
            e.printStackTrace();
        }

    }
}