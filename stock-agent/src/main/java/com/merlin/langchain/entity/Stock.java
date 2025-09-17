package com.merlin.langchain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stock {
    private Long id;
    private String stockCode;
    private String stockName;
    private String curPrice;
    private String date;
    private String time;
    private String detailInfo;
}
