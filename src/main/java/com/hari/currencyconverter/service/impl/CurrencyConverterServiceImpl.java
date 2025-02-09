package com.hari.currencyconverter.service.impl;

import com.hari.currencyconverter.client.CurrencyConverterFeignClient;
import com.hari.currencyconverter.dto.request.ConvertRequestDto;
import com.hari.currencyconverter.dto.response.ConvertResponseDto;
import com.hari.currencyconverter.dto.response.RatesBaseResponseDto;
import com.hari.currencyconverter.dto.response.RatesDto;
import com.hari.currencyconverter.dto.openexchangerates.CurrencyRatesResponse;
import com.hari.currencyconverter.service.CurrencyConverterService;
import feign.FeignException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Service
public class CurrencyConverterServiceImpl implements CurrencyConverterService {

    @Autowired
    private CurrencyConverterFeignClient currencyConverterFeignClient;

    @Value("${currency.converter.apiKey}")
    public String apiKey;

    @Override
    public RatesBaseResponseDto getRates(String base) throws BadRequestException {
        RatesBaseResponseDto ratesBaseResponseDto;
        try {
            CurrencyRatesResponse ratesResponse = currencyConverterFeignClient.getRates(apiKey, base);
            ratesBaseResponseDto = getRatesBaseResponseDto(ratesResponse);
        }catch (FeignException e){
            if(e.getMessage().contains("unsupported-code")) {
                throw new BadRequestException("Invalid currency code!");
            }else {
                throw new BadRequestException(e.getMessage());
            }
        }
        return ratesBaseResponseDto;
    }

    @Override
    public ConvertResponseDto convertCurrency(ConvertRequestDto convertRequestDto) throws BadRequestException {
        ConvertResponseDto convertResponseDto = new ConvertResponseDto();
        try {
            if(Objects.isNull(convertRequestDto.getAmount()) || convertRequestDto.getAmount() <= 0){
                throw new BadRequestException("Invalid amount!");
            }

            CurrencyRatesResponse ratesResponse = currencyConverterFeignClient.getRates(apiKey, convertRequestDto.getFrom());
            Double rate = ratesResponse.getRates().get(convertRequestDto.getTo());
            if (Objects.isNull(rate)) {
                throw new BadRequestException("Invalid To currency code!");
            }
            convertResponseDto.setFrom(convertRequestDto.getFrom());
            convertResponseDto.setTo(convertRequestDto.getTo());
            convertResponseDto.setAmount(convertRequestDto.getAmount());
            Double convertedAmount = BigDecimal.valueOf(convertRequestDto.getAmount() * rate)
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
            convertResponseDto.setConvertedAmount(convertedAmount);
        } catch (FeignException e) {
            if (e.getMessage().contains("unsupported-code")) {
                throw new BadRequestException("Invalid From currency code!");
            } else {
                throw new BadRequestException(e.getMessage());
            }
        }
        return convertResponseDto;
    }

    private static RatesBaseResponseDto getRatesBaseResponseDto(CurrencyRatesResponse ratesResponse) {
        RatesBaseResponseDto ratesBaseResponseDto = new RatesBaseResponseDto();
        ratesBaseResponseDto.setBase(ratesResponse.getBase());
        List<RatesDto> ratesDtoList = ratesResponse.getRates().entrySet().stream().map(entry -> {
            RatesDto ratesDto = new RatesDto();
            ratesDto.setCurrency(entry.getKey());
            ratesDto.setRate(entry.getValue());
            return ratesDto;
        }).toList();
        ratesBaseResponseDto.setRates(ratesDtoList);
        return ratesBaseResponseDto;
    }
}
