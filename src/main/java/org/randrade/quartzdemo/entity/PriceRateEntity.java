package org.randrade.quartzdemo.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
public class PriceRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private BigDecimal price;

    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flightDetails_id")
    private FlightDetailsEntity flightDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flightSchedule_id")
    private FlightScheduleEntity flightSchedule;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public FlightDetailsEntity getFlightDetails() {
        return flightDetails;
    }

    public void setFlightDetails(FlightDetailsEntity flightDetails) {
        this.flightDetails = flightDetails;
    }

    public FlightScheduleEntity getFlightSchedule() {
        return flightSchedule;
    }

    public void setFlightSchedule(FlightScheduleEntity flightSchedule) {
        this.flightSchedule = flightSchedule;
    }
}
