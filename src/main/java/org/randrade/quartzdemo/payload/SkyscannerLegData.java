package org.randrade.quartzdemo.payload;

import java.util.ArrayList;
import java.util.List;

public class SkyscannerLegData{

    private String id;
    private SkyscannerPlaceData originStation;
    private SkyscannerPlaceData destinationStation;
    private String departure;
    private String arrival;
    private int duration;
    private List<SkyscannerCarrierData> carriers = new ArrayList<>();
    private List<String> flightNumber = new ArrayList<>();


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SkyscannerPlaceData getOriginStation() {
        return originStation;
    }

    public void setOriginStation(SkyscannerPlaceData originStation) {
        this.originStation = originStation;
    }

    public SkyscannerPlaceData getDestinationStation() {
        return destinationStation;
    }

    public void setDestinationStation(SkyscannerPlaceData destinationStation) {
        this.destinationStation = destinationStation;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<SkyscannerCarrierData> getCarriers() {
        return carriers;
    }

    public void setCarriers(List<SkyscannerCarrierData> carriers) {
        this.carriers = carriers;
    }

    public List<String> getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(List<String> flightNumber) {
        this.flightNumber = flightNumber;
    }

    @Override
    public String toString() {
        return "SkyscannerLegData{" +
                "id='" + id + '\'' +
                ", originStation=" + originStation +
                ", destinationStation=" + destinationStation +
                ", departure='" + departure + '\'' +
                ", arrival='" + arrival + '\'' +
                ", duration=" + duration +
                ", carriers=" + carriers +
                ", flightNumber=" + flightNumber +
                '}';
    }
}