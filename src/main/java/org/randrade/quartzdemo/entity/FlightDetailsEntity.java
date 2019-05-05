package org.randrade.quartzdemo.entity;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
public class FlightDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String company;

    private Date flightDate;

    private String flightNumber;

    @OneToMany(
            mappedBy = "flightDetails",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PriceRateEntity> priceRates = new ArrayList<>();


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Date getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(Date flightDate) {
        this.flightDate = flightDate;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public List<PriceRateEntity> getPriceRates() {
        return priceRates;
    }

    public void setPriceRates(List<PriceRateEntity> priceRates) {
        this.priceRates = priceRates;
    }
}
