package nl.fontys.s3.starter.controller;

import nl.fontys.s3.starter.business.GetTickerPricesUseCase;
import nl.fontys.s3.starter.domain.GetTickerPricesRequest;
import nl.fontys.s3.starter.domain.GetTickerPricesResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tickers/prices")
public class TickerPriceController {
    private final GetTickerPricesUseCase getTickerPricesUseCase;

    public TickerPriceController(GetTickerPricesUseCase getTickerPricesUseCase) {
        this.getTickerPricesUseCase = getTickerPricesUseCase;
    }

    @GetMapping
    public ResponseEntity<GetTickerPricesResponse> getTickerPrice(@RequestParam("from") String fromCurrency,
                                                                  @RequestParam("to") String toCurrency) {
        GetTickerPricesRequest getTickerPricesRequestDTO = GetTickerPricesRequest.builder()
                .fromCurrency(fromCurrency)
                .toCurrency(toCurrency)
                .build();
        GetTickerPricesResponse responseBody = getTickerPricesUseCase.getTickerPrices(getTickerPricesRequestDTO);
        return ResponseEntity.ok(responseBody);
    }

}
