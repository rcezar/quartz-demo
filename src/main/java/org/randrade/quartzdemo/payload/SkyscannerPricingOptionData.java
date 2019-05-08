package org.randrade.quartzdemo.payload;

import java.math.BigDecimal;

public class SkyscannerPricingOptionData {

    private String agent;
    private BigDecimal price;
    private String currency;

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "SkyscannerPricingOptionData{" +
                "agent='" + agent + '\'' +
                ", price=" + price +
                ", currency='" + currency + '\'' +
                '}';
    }
}

