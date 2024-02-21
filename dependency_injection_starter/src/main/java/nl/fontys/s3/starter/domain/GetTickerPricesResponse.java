package nl.fontys.s3.starter.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class GetTickerPricesResponse {
    private String fromCurrency;
    private String toCurrency;
    private List<TickerPrice> currentPrices;
}
