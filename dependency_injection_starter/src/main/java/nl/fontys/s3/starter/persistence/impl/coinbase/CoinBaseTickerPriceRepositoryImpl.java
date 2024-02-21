package nl.fontys.s3.starter.persistence.impl.coinbase;

import lombok.AllArgsConstructor;
import nl.fontys.s3.starter.domain.TickerPrice;
import nl.fontys.s3.starter.persistence.TickerPriceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Repository
@AllArgsConstructor
@Primary
public class CoinBaseTickerPriceRepositoryImpl implements TickerPriceRepository {

    private static final String COINBASE_TICKER_PRICE_URL = "https://api.coinbase.com/v2/prices/{from}-{to}/buy";
    private static final Logger LOGGER = LoggerFactory.getLogger(CoinBaseTickerPriceRepositoryImpl.class);
    private RestTemplate restTemplate;


    @Override
    public TickerPrice getCurrentPrice(String fromCurrency, String toCurrency) {
        try {
            Map<String, String> params = Map.of("from", fromCurrency, "to", toCurrency);

            LOGGER.info("Calling CoinBase API for ticker {} price.", params);
            CoinBaseTickerPrice tickerPrice = restTemplate.getForObject(COINBASE_TICKER_PRICE_URL, CoinBaseTickerPrice.class, params);
            LOGGER.info("Called CoinBase API for ticker {} price with success.", params);

            if (tickerPrice == null || tickerPrice.getData() == null || tickerPrice.getData().getAmount() == null) {
                LOGGER.warn("Empty response {} for {}", tickerPrice, params);
                return null;
            }

            return TickerPrice.builder()
                    .exchangeName("CoinBase")
                    .price(tickerPrice.getData().getAmount())
                    .build();
        } catch (HttpClientErrorException e) {
            LOGGER.error("Error calling CoinBase API: " + e.getResponseBodyAsString(), e);
            return null;
        }
    }
}
