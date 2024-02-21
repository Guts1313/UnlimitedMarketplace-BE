package nl.fontys.s3.starter.persistence.impl.binance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceTickerPrice {
    private String symbol;
    private Double price;
}
