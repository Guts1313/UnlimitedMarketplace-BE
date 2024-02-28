package nl.fontys.s3.starter.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TickerPrice {
    private final Double price;
    private final String exchangeName;
}
