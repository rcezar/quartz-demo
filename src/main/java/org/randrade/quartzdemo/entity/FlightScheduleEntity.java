package org.randrade.quartzdemo.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


@Entity
public class FlightScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String inbound;

    private String outbound;

    private BigDecimal targetRate;

    private Boolean flexibleDate;

    private Date inboundDate;

    private Date outboundDate;

    @OneToMany(
            mappedBy = "flightSchedule",
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

    public String getInbound() {
        return inbound;
    }

    public void setInbound(String inbound) {
        this.inbound = inbound;
    }

    public String getOutbound() {
        return outbound;
    }

    public void setOutbound(String outbound) {
        this.outbound = outbound;
    }

    public BigDecimal getTargetRate() {
        return targetRate;
    }

    public void setTargetRate(BigDecimal targetRate) {
        this.targetRate = targetRate;
    }

    public Boolean getFlexibleDate() {
        return flexibleDate;
    }

    public void setFlexibleDate(Boolean flexibleDate) {
        this.flexibleDate = flexibleDate;
    }

    public Date getInboundDate() {
        return inboundDate;
    }

    public void setInboundDate(Date inboundDate) {
        this.inboundDate = inboundDate;
    }

    public Date getOutboundDate() {
        return outboundDate;
    }

    public void setOutboundDate(Date outboundDate) {
        this.outboundDate = outboundDate;
    }

    public List<PriceRateEntity> getPriceRates() {
        return priceRates;
    }

    public void setPriceRates(List<PriceRateEntity> priceRates) {
        this.priceRates = priceRates;
    }
}

