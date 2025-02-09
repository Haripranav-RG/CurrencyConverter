package com.hari.currencyconverter.dto.openexchangerates;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class CurrencyRatesResponse {
    @JsonProperty("base_code")
    private String base;
    @JsonProperty("conversion_rates")
    private Map<String, Double> rates;
}
