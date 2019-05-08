package org.randrade.quartzdemo.service;


import com.mashape.unirest.http.exceptions.UnirestException;
import org.randrade.quartzdemo.entity.FlightScheduleEntity;
import org.randrade.quartzdemo.model.FlightSearchRequest;
import org.randrade.quartzdemo.payload.SkyScannerData;

import java.util.List;

public interface SkyScannerService {

    FlightScheduleEntity createSession(FlightSearchRequest request) throws UnirestException;

    List<SkyScannerData> getSessionResults(String session) throws UnirestException;
}
