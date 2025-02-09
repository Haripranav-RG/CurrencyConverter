package com.hari.currencyconverter.service;

import com.hari.currencyconverter.client.CurrencyConverterFeignClient;
import com.hari.currencyconverter.dto.request.ConvertRequestDto;
import com.hari.currencyconverter.dto.response.ConvertResponseDto;
import com.hari.currencyconverter.dto.response.RatesBaseResponseDto;
import com.hari.currencyconverter.dto.openexchangerates.CurrencyRatesResponse;
import com.hari.currencyconverter.service.impl.CurrencyConverterServiceImpl;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyConverterServiceTest {

    @Mock
    private CurrencyConverterFeignClient currencyConverterFeignClient;

    @InjectMocks
    private CurrencyConverterServiceImpl currencyConverterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currencyConverterService.apiKey = "testAppId";
    }

    @Test
    void testGetRates_Success() throws BadRequestException {
        String base = "USD";
        CurrencyRatesResponse ratesResponse = new CurrencyRatesResponse();
        ratesResponse.setBase(base);
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.85);
        ratesResponse.setRates(rates);

        when(currencyConverterFeignClient.getRates("testAppId", base)).thenReturn(ratesResponse);

        RatesBaseResponseDto result = currencyConverterService.getRates(base);

        assertNotNull(result);
        assertEquals(base, result.getBase());
        assertEquals(1, result.getRates().size());
        assertEquals("EUR", result.getRates().get(0).getCurrency());
        assertEquals(0.85, result.getRates().get(0).getRate());
    }

    @Test
    void testGetRates_InvalidBaseCurrency() {
        String base = "INVALID";

        String message = "unsupported-code";
        Request request = Request.create(Request.HttpMethod.GET, "/example", new HashMap<>(), null, new RequestTemplate());
        Response response = Response.builder()
                .status(400)
                .reason(message)
                .request(request)
                .body(message, StandardCharsets.UTF_8)
                .build();

        FeignException feignException = FeignException.errorStatus("POST", response);

        when(currencyConverterFeignClient.getRates("testAppId", base)).thenThrow(feignException);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> currencyConverterService.getRates(base));

        assertEquals("Invalid currency code!", exception.getMessage());
    }

    @Test
    void testGetRates_OtherFeignException() {
        String base = "USD";

        String message = "Custom error message";
        Request request = Request.create(Request.HttpMethod.GET, "/example", new HashMap<>(), null, new RequestTemplate());
        Response response = Response.builder()
                .status(400)
                .reason(message)
                .request(request)
                .body(message, StandardCharsets.UTF_8)
                .build();

        FeignException feignException = FeignException.errorStatus("POST", response);

        when(currencyConverterFeignClient.getRates("testAppId", base)).thenThrow(feignException);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            currencyConverterService.getRates(base);
        });

        assertNotEquals("Invalid base currency code!", exception.getMessage());
    }

    @Test
    void testConvertCurrency_Success() throws BadRequestException {
        ConvertRequestDto requestDto = new ConvertRequestDto();
        requestDto.setFrom("USD");
        requestDto.setTo("EUR");
        requestDto.setAmount(100.0);

        CurrencyRatesResponse ratesResponse = new CurrencyRatesResponse();
        ratesResponse.setBase("USD");
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.85);
        ratesResponse.setRates(rates);

        when(currencyConverterFeignClient.getRates("testAppId", "USD")).thenReturn(ratesResponse);

        ConvertResponseDto responseDto = currencyConverterService.convertCurrency(requestDto);

        assertNotNull(responseDto);
        assertEquals("USD", responseDto.getFrom());
        assertEquals("EUR", responseDto.getTo());
        assertEquals(100.0, responseDto.getAmount());
        assertEquals(85.0, responseDto.getConvertedAmount());
    }

    @Test
    void testConvertCurrency_InvalidToCurrency() {
        ConvertRequestDto requestDto = new ConvertRequestDto();
        requestDto.setFrom("USD");
        requestDto.setTo("INVALID");
        requestDto.setAmount(100.0);

        CurrencyRatesResponse ratesResponse = new CurrencyRatesResponse();
        ratesResponse.setBase("USD");
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.85);
        ratesResponse.setRates(rates);

        when(currencyConverterFeignClient.getRates("testAppId", "USD")).thenReturn(ratesResponse);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            currencyConverterService.convertCurrency(requestDto);
        });

        assertEquals("Invalid To currency code!", exception.getMessage());
    }

    @Test
    void testConvertCurrency_InvalidFromCurrency() {
        ConvertRequestDto requestDto = new ConvertRequestDto();
        requestDto.setFrom("INVALID");
        requestDto.setTo("EUR");
        requestDto.setAmount(100.0);

        String message = "unsupported-code";
        Request request = Request.create(Request.HttpMethod.GET, "/example", new HashMap<>(), null, new RequestTemplate());
        Response response = Response.builder()
                .status(400)
                .reason(message)
                .request(request)
                .body(message, StandardCharsets.UTF_8)
                .build();

        FeignException feignException = FeignException.errorStatus("POST", response);

        when(currencyConverterFeignClient.getRates("testAppId", "INVALID")).thenThrow(feignException);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            currencyConverterService.convertCurrency(requestDto);
        });

        assertEquals("Invalid From currency code!", exception.getMessage());
    }

    @Test
    void testConvertCurrency_OtherFeignException() {
        ConvertRequestDto requestDto = new ConvertRequestDto();
        requestDto.setFrom("USD");
        requestDto.setTo("EUR");
        requestDto.setAmount(100.0);

        String message = "Custom error message";
        Request request = Request.create(Request.HttpMethod.GET, "/example", new HashMap<>(), null, new RequestTemplate());
        Response response = Response.builder()
                .status(400)
                .reason(message)
                .request(request)
                .body(message, StandardCharsets.UTF_8)
                .build();

        FeignException feignException = FeignException.errorStatus("POST", response);

        when(currencyConverterFeignClient.getRates("testAppId", "USD")).thenThrow(feignException);

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                currencyConverterService.convertCurrency(requestDto));

        assertNotEquals("Invalid From currency code!", exception.getMessage());
    }
}