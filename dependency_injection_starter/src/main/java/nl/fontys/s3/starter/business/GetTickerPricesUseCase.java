package nl.fontys.s3.starter.business;

import nl.fontys.s3.starter.domain.GetTickerPricesRequest;
import nl.fontys.s3.starter.domain.GetTickerPricesResponse;

public interface GetTickerPricesUseCase {
    GetTickerPricesResponse getTickerPrices(GetTickerPricesRequest request);
}
