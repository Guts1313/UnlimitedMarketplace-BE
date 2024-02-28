package nl.fontys.s3.starter.persistence;

import nl.fontys.s3.starter.domain.TickerPrice;

public interface TickerPriceRepository {
    TickerPrice getCurrentPrice(String fromCurrency, String toCurrency);
}
