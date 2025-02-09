package com.hari.currencyconverter.dto.request;

import lombok.Data;

@Data
public class ConvertRequestDto {

    private String from;
    private String to;
    private Double amount;
}
