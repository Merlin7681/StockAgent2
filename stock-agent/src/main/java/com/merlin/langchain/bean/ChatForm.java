package com.merlin.langchain.bean;

import lombok.Data;

@Data
public class ChatForm {
    private Long memoryId;// 对话 id
    private String message;// 用户问题
}

