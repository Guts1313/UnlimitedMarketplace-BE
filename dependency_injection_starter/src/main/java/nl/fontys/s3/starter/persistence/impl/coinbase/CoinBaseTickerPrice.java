package nl.fontys.s3.starter.persistence.impl.coinbase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinBaseTickerPrice {
    private Data data;

    @Setter
    @Getter
    static class Data {
        private String base;
        private String currency;
        private Double amount;
    }
}
