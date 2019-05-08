package org.randrade.quartzdemo.payload;

import java.util.ArrayList;
import java.util.List;

public class SkyScannerData {

    private SkyscannerLegData outboundLeg = new SkyscannerLegData();
    private SkyscannerLegData inboundLeg = new SkyscannerLegData();
    private List<SkyscannerPricingOptionData> pricingOptionDataList = new ArrayList<>();

    public SkyscannerLegData getOutboundLeg() {
        return outboundLeg;
    }

    public void setOutboundLeg(SkyscannerLegData outboundLeg) {
        this.outboundLeg = outboundLeg;
    }

    public SkyscannerLegData getInboundLeg() {
        return inboundLeg;
    }

    public void setInboundLeg(SkyscannerLegData inboundLeg) {
        this.inboundLeg = inboundLeg;
    }

    public List<SkyscannerPricingOptionData> getPricingOptionDataList() {
        return pricingOptionDataList;
    }

    public void setPricingOptionDataList(List<SkyscannerPricingOptionData> pricingOptionDataList) {
        this.pricingOptionDataList = pricingOptionDataList;
    }

    @Override
    public String toString() {
        return "SkyScannerData{" +
                "outboundLeg=" + outboundLeg +
                ", inboundLeg=" + inboundLeg +
                ", pricingOptionDataList=" + pricingOptionDataList +
                '}';
    }
}





