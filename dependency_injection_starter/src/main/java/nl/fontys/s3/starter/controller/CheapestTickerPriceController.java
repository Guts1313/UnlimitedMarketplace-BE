package nl.fontys.s3.starter.controller;

import nl.fontys.s3.starter.business.GetCheapestPriceUseCase;
import nl.fontys.s3.starter.domain.GetCheapestPriceRequest;
import nl.fontys.s3.starter.domain.GetTickerPricesRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class CheapestTickerPriceController {
    private final GetCheapestPriceUseCase getCheapestTickerPriceUseCase;

    @Autowired

    public CheapestTickerPriceController(GetCheapestPriceUseCase getCheapestTickerPriceUseCase) {
        this.getCheapestTickerPriceUseCase = getCheapestTickerPriceUseCase;
    }

    @GetMapping("/tickers/cheapest/{fromCurrency}/{toCurrency}")
    public ResponseEntity<?> getCheapestTickerPrice(@PathVariable String fromCurrency, @PathVariable String toCurrency) {
        GetCheapestPriceRequest request = new GetCheapestPriceRequest(fromCurrency, toCurrency);
        return ResponseEntity.ok(getCheapestTickerPriceUseCase.getCheapestTickerPrice(request));
    }

}
