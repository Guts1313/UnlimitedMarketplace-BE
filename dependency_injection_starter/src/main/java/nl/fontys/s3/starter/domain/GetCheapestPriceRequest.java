package nl.fontys.s3.starter.domain;

public class GetCheapestPriceRequest {
    private String fromCurrency;
    private String toCurrency;

    // Constructor, Getters and Setters
    public GetCheapestPriceRequest(String fromCurrency, String toCurrency) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
    }

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

}
