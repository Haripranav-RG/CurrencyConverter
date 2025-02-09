package com.hari.currencyconverter.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class RatesBaseResponseDto {

    private String base;
    private List<RatesDto> rates;

}
