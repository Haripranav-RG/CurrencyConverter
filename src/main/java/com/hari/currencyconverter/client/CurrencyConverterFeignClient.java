package com.hari.currencyconverter.client;

import com.hari.currencyconverter.dto.openexchangerates.CurrencyRatesResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "currency-converter", url = "https://v6.exchangerate-api.com")
public interface CurrencyConverterFeignClient {

    @GetMapping("/v6/{apiKey}/latest/{base}")
    CurrencyRatesResponse getRates(@PathVariable("apiKey") String apiKey, @PathVariable("base") String base);
}