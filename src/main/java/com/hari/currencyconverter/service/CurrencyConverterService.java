package com.hari.currencyconverter.service;

import com.hari.currencyconverter.dto.request.ConvertRequestDto;
import com.hari.currencyconverter.dto.response.ConvertResponseDto;
import com.hari.currencyconverter.dto.response.RatesBaseResponseDto;
import org.apache.coyote.BadRequestException;

public interface CurrencyConverterService {
    RatesBaseResponseDto getRates(String base) throws BadRequestException;

    ConvertResponseDto convertCurrency(ConvertRequestDto convertRequestDto) throws BadRequestException;
}
