package nl.fontys.s3.starter.persistence.impl.binance;

import lombok.AllArgsConstructor;
import nl.fontys.s3.starter.domain.TickerPrice;
import nl.fontys.s3.starter.persistence.TickerPriceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Repository
@AllArgsConstructor
@Qualifier("binanceRepo")
public class BinanceTickerPriceRepositoryImpl implements TickerPriceRepository {

    private static final String BINANCE_TICKER_PRICE_URL = "https://api2.binance.com/api/v3/ticker/price?symbol={symbol}";
    private static final Logger LOGGER = LoggerFactory.getLogger(BinanceTickerPriceRepositoryImpl.class);
    private RestTemplate restTemplate;


    @Override
    public TickerPrice getCurrentPrice(String fromCurrency, String toCurrency) {
        try {
            Map<String, String> params = Map.of("symbol", fromCurrency + toCurrency);

            LOGGER.info("Calling Binance API for ticker {} price.", params);
            BinanceTickerPrice tickerPrice = restTemplate.getForObject(BINANCE_TICKER_PRICE_URL, BinanceTickerPrice.class, params);
            LOGGER.info("Called Binance API for ticker {} price with success.", params);

            if (tickerPrice == null || tickerPrice.getPrice() == null) {
                LOGGER.warn("Empty response {} for {}", tickerPrice, params);
                return null;
            }

            return TickerPrice.builder()
                    .exchangeName("Binance")
                    .price(tickerPrice.getPrice())
                    .build();
        } catch (HttpClientErrorException e) {
            LOGGER.error("Error calling Binance API: " + e.getResponseBodyAsString(), e);
            return null;
        }
    }
}
