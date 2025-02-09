package com.hari.currencyconverter.controller;

import com.hari.currencyconverter.dto.request.ConvertRequestDto;
import com.hari.currencyconverter.dto.response.ConvertResponseDto;
import com.hari.currencyconverter.dto.response.RatesBaseResponseDto;
import com.hari.currencyconverter.service.CurrencyConverterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Currency Converter API", description = "Endpoints for currency conversion and exchange rates.")

public class CurrencyConverterController {

    @Autowired
    private CurrencyConverterService currencyConverterService;

    @Operation(summary = "Get exchange rates", description = "Retrieves exchange rates for a given base currency.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of exchange rates", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RatesBaseResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @GetMapping("/rates")
    public ResponseEntity<RatesBaseResponseDto> getRates(
            @Parameter(description = "Base currency code (e.g., USD, EUR)", example = "USD")
            @RequestParam(name = "base", required = false, defaultValue = "USD") String base) throws BadRequestException {

        return ResponseEntity.ok(currencyConverterService.getRates(base));

    }

    @Operation(summary = "Convert currency", description = "Converts an amount from one currency to another.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful currency conversion", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ConvertResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PostMapping("/convert")
    public ResponseEntity<ConvertResponseDto> convertCurrency(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request body for currency conversion", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ConvertRequestDto.class)))
            @RequestBody ConvertRequestDto convertRequestDto) throws BadRequestException {
        return ResponseEntity.ok(currencyConverterService.convertCurrency(convertRequestDto));
    }
}