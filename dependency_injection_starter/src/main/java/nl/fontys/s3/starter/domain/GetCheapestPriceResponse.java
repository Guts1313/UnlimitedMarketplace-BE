package nl.fontys.s3.starter.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GetCheapestPriceResponse {
    private String fromCurrency;
    private String toCurrency;
    private Double cheapestPrice;
    private String cheapestExchange;
    private Double savings;



    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public Double getCheapestPrice() {
        return cheapestPrice;
    }

    public void setCheapestPrice(Double cheapestPrice) {
        this.cheapestPrice = cheapestPrice;
    }

    public String getCheapestExchange() {
        return cheapestExchange;
    }

    public void setCheapestExchange(String cheapestExchange) {
        this.cheapestExchange = cheapestExchange;
    }

    public Double getSavings() {
        return savings;
    }

    public void setSavings(Double savings) {
        this.savings = savings;
    }
}
