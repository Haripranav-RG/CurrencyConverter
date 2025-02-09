package com.hari.currencyconverter.dto.response;

import lombok.Data;

@Data
public class ConvertResponseDto {

    private String from;
    private String to;
    private Double amount;
    private Double convertedAmount;
}
