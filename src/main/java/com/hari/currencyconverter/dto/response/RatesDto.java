package com.hari.currencyconverter.dto.response;

import lombok.Data;

@Data
public class RatesDto {

    private String currency;
    private Double rate;

}
