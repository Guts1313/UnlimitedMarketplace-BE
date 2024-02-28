package nl.fontys.s3.starter.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GetTickerPricesRequest {
    private String fromCurrency;
    private String toCurrency;
}
