package nl.fontys.s3.starter.business;

import nl.fontys.s3.starter.domain.GetCheapestPriceRequest;
import nl.fontys.s3.starter.domain.GetCheapestPriceResponse;

public interface GetCheapestPriceUseCase {
    GetCheapestPriceResponse getCheapestTickerPrice(GetCheapestPriceRequest request);

}
