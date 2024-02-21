package nl.fontys.s3.starter.business.impl;

import nl.fontys.s3.starter.business.GetCheapestPriceUseCase;
import nl.fontys.s3.starter.domain.GetCheapestPriceRequest;
import nl.fontys.s3.starter.domain.GetCheapestPriceResponse;
import nl.fontys.s3.starter.domain.TickerPrice;
import nl.fontys.s3.starter.persistence.TickerPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class GetCheapestTickerPriceUseCaseImpl implements GetCheapestPriceUseCase {

    private final List<TickerPriceRepository> tickerPriceRepositories;
    private static final Logger LOGGER = LoggerFactory.getLogger(GetCheapestTickerPriceUseCaseImpl.class);


    @Autowired
    public GetCheapestTickerPriceUseCaseImpl(List<TickerPriceRepository> tickerPriceRepositories) {
        this.tickerPriceRepositories = tickerPriceRepositories;
    }


    @Override
    public GetCheapestPriceResponse getCheapestTickerPrice(GetCheapestPriceRequest request) {
        List<TickerPrice> prices = tickerPriceRepositories.stream()
                .map(repo -> repo.getCurrentPrice(request.getFromCurrency(), request.getToCurrency()))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        // For debugging purposes: print out each price obtained
        prices.forEach(price -> LOGGER.info("Price from {}: {}", price.getExchangeName(), price.getPrice()));

        TickerPrice cheapest = prices.stream()
                .min(Comparator.comparing(TickerPrice::getPrice))
                .orElse(null);

        double maxPrice = prices.stream()
                .max(Comparator.comparing(TickerPrice::getPrice))
                .map(TickerPrice::getPrice)
                .orElse(0.0);

        // Debugging: print out the cheapest and most expensive prices for comparison
        if (cheapest != null) {
            LOGGER.info("Cheapest price for {}/{} is {} at {}", request.getFromCurrency(), request.getToCurrency(), cheapest.getPrice(), cheapest.getExchangeName());
            LOGGER.info("Highest price for {}/{} is {}", request.getFromCurrency(), request.getToCurrency(), maxPrice);
            LOGGER.info("Savings if using cheapest exchange: {}", maxPrice - cheapest.getPrice());
        } else {
            LOGGER.info("No prices found for {}/{}", request.getFromCurrency(), request.getToCurrency());
        }

        return new GetCheapestPriceResponse(
                request.getFromCurrency(),
                request.getToCurrency(),
                cheapest != null ? cheapest.getPrice() : null,
                cheapest != null ? cheapest.getExchangeName() : null,
                cheapest != null ? maxPrice - cheapest.getPrice() : null
        );
    }
}