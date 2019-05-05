package org.randrade.quartzdemo.service.impl;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.randrade.quartzdemo.service.SkyScannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class SkyScannerServiceImpl implements SkyScannerService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void getQuote() {




    }


    private String createSession() throws UnirestException {
        String url = "https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/pricing/v1.0";

        HttpResponse<JsonNode> jsonResponse = Unirest.post(url)
                .header("X-RapidAPI-Host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com")
                .header("X-RapidAPI-Key", "48e90d683emsh832f24b745fa793p1ea4e7jsnf4381c3f6d91")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("accept", "application/json")
                //.queryString("apiKey", "123")
                .field("country", "US")
                .field("currency", "USD")
                .field("locale", "en-US")
                .field("originPlace", "SFO-sky")
                .field("destinationPlace", "LHR-sky")
                .field("inboundDate", "2019-09-05")
                .field("outboundDate", "2019-09-02")
                .field("adults", "1")
                .asJson();

        if (HttpStatus.CREATED.value() == jsonResponse.getStatus()) {

            List<String> locationList = jsonResponse.getHeaders().get("Location");

            if (!CollectionUtils.isEmpty(locationList)) {

                String[] l = locationList.get(0).split("/");

                if (l != null && l.length > 0) {

                    return l[l.length - 1];
                }
            }
        }

        return null;

    }
}
