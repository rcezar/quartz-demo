package org.randrade.quartzdemo.service.impl;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.randrade.quartzdemo.model.FlightSearchRequest;
import org.randrade.quartzdemo.payload.SkyScannerData;

import java.util.List;

public class SkyScannerServiceTest {

    @InjectMocks
    private SkyScannerServiceImpl service;

    @Test
    public void testUnirest() throws UnirestException {


        service.createSession(new FlightSearchRequest());

    }
}
