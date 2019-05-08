package org.randrade.quartzdemo.service.impl;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.randrade.quartzdemo.entity.FlightScheduleEntity;
import org.randrade.quartzdemo.model.FlightSearchRequest;
import org.randrade.quartzdemo.payload.*;
import org.randrade.quartzdemo.repository.FlightScheduleRepository;
import org.randrade.quartzdemo.service.SkyScannerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SkyScannerServiceImpl implements SkyScannerService {

    private static final Logger logger = LoggerFactory.getLogger(SkyScannerServiceImpl.class);

    @Autowired
    private FlightScheduleRepository repository;

    @Value("${rapidapi.url.create-session}")
    private String createSessionUrl;

    @Value("${rapidapi.url.get-session-results}")
    private String queryResultsUrl;

    @Value("${rapidapi.host}")
    private String rapidApiHost;

    @Value("${rapidapi.key}")
    private String rapidApiKey;

    private JSONArray places;
    private JSONArray carriers;

    private Map<Integer, SkyscannerPlaceData> skyscannerPlaceData;
    private Map<Integer, SkyscannerCarrierData> skyscannerCarrierData;

    @Override
    public FlightScheduleEntity createSession(FlightSearchRequest request) throws UnirestException {

        logger.info("Creating session for Skyscanner on Rapid API....");

        HttpResponse<JsonNode> jsonResponse = Unirest.post(createSessionUrl)
                .header("X-RapidAPI-Host", rapidApiHost)
                .header("X-RapidAPI-Key", rapidApiKey)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("accept", "application/json")
                .field("country", "US")
                .field("currency", "USD")
                .field("locale", "en-US")
                .field("originPlace", request.getOriginPlace())
                .field("destinationPlace", request.getDestinationPlace())
                .field("inboundDate", request.getInboundDate())
                .field("outboundDate", request.getOutboundDate())
                .field("adults", "1")
                .asJson();

        if (HttpStatus.CREATED.value() == jsonResponse.getStatus()) {

            List<String> locationList = jsonResponse.getHeaders().get("Location");

            String[] l  = locationList.get(0).split("/");

            String session = l[l.length-1];

            FlightScheduleEntity entity = new FlightScheduleEntity();
            entity.setFlexibleDate(false);
            entity.setInbound(request.getOriginPlace());
            entity.setOutbound(request.getDestinationPlace());
            entity.setInboundDate(Date.valueOf(request.getInboundDate()));
            entity.setOutboundDate(Date.valueOf(request.getOutboundDate()));
            entity.setSession(session);
            //entity.getPriceRates().addAll(convertPrices(sessionResults));

            repository.save(entity);

            return entity;
        }

        logger.error("Nao foi possivel criar um session id... {}", jsonResponse.getBody().toString());

        return null;

    }


    @Override
    public List<SkyScannerData> getSessionResults(String session) throws UnirestException {

        logger.info("Getting session results for {} for Skyscanner on Rapid API....", session);

        HttpResponse<JsonNode> jsonResponse = Unirest.get(queryResultsUrl + session )
                .header("X-RapidAPI-Host", rapidApiHost)
                .header("X-RapidAPI-Key", rapidApiKey)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("accept", "application/json")
                .queryString("sortType", "price")
                .queryString("sortOrder", "desc")
                .queryString("pageIndex", "0")
                .queryString("pageSize", "10")
                .asJson();

         return convert(jsonResponse.getBody());
    }

    private List<SkyScannerData> convert(JsonNode body){

        List<SkyScannerData> list = new ArrayList<>();

        skyscannerPlaceData = new HashMap<>();
        skyscannerCarrierData = new HashMap<>();

        setPlaces((JSONArray) body.getObject().get("Places"));
        setCarriers((JSONArray) body.getObject().get("Carriers"));

        JSONArray legs = (JSONArray) body.getObject().get("Legs");

        JSONArray itineraries = (JSONArray) body.getObject().get("Itineraries");

        for (int i=0; i < itineraries.length(); i++) {

            JSONObject in = itineraries.getJSONObject(i);

            SkyScannerData d = new SkyScannerData();
            d.setInboundLeg(getLegById(in.getString("InboundLegId"), legs));
            d.setOutboundLeg(getLegById(in.getString("OutboundLegId"), legs));

            JSONArray pricingOptions = (JSONArray) in.get("PricingOptions");
            if (pricingOptions != null) {

                for (int j = 0; j < pricingOptions.length(); j++) {

                    SkyscannerPricingOptionData poD = new SkyscannerPricingOptionData();
                    JSONObject po = pricingOptions.getJSONObject(j);

                    poD.setPrice(new BigDecimal((Double) po.get("Price")));
                    d.getPricingOptionDataList().add(poD);
                }
            }

            list.add(d);
        }


        return list;
    }

    private SkyscannerLegData getLegById(String legId, JSONArray legs) {

        if (!StringUtils.isEmpty(legId)) {

            for (int i = 0; i < legs.length(); i++) {

                JSONObject obj = legs.getJSONObject(i);

                if (legId.equalsIgnoreCase(obj.getString("Id"))) {
                    SkyscannerLegData leg = new SkyscannerLegData();

                    leg.setId(legId);
                    leg.setArrival(obj.getString("Arrival"));
                    leg.setDeparture(obj.getString("Departure"));
                    leg.setDuration(obj.getInt("Duration"));

                    leg.setDestinationStation(getPlacesByID(obj.getInt("DestinationStation")));
                    leg.setOriginStation(getPlacesByID(obj.getInt("OriginStation")));

                    JSONArray carriers = obj.getJSONArray("Carriers");
                    for (int t = 0; t < carriers.length(); t++) {
                        leg.getCarriers().add(getCarriersByID(carriers.getInt(t)));
                    }

                    JSONArray flight = obj.getJSONArray("FlightNumbers");
                    for (int j = 0; j < flight.length(); j++) {

                        JSONObject objFlight = flight.getJSONObject(j);
                        leg.getFlightNumber().add(objFlight.getString("FlightNumber"));
                    }

                    return leg;
                }
            }
        }

        return null;
    }

    private SkyscannerCarrierData getCarriersByID(Integer carrierId) {

        if (carrierId != null) {

            SkyscannerCarrierData result = skyscannerCarrierData.get(carrierId);

            if (result != null) {

                return result;

            }else{

                JSONArray carriers = getCarriers();

                if (carriers != null) {

                    for (int j = 0; j < carriers.length(); j++) {

                        JSONObject obj = carriers.getJSONObject(j);

                        Integer id = obj.getInt("Id");

                        if (carrierId.equals(id)) {

                            result = new SkyscannerCarrierData();
                            result.setCode(obj.getString("Code"));
                            result.setName(obj.getString("Name"));
                            skyscannerCarrierData.put(id, result);

                            return result;
                        }
                    }
                }
            }
        }

        return null;
    }

    private SkyscannerPlaceData getPlacesByID(Integer placeId) {

        if (placeId != null) {

            SkyscannerPlaceData result = skyscannerPlaceData.get(placeId);

            if (result != null) {

                return result;

            }else{

                JSONArray places = getPlaces();

                if (places != null) {
                    for (int j = 0; j < places.length(); j++) {

                        JSONObject obj = places.getJSONObject(j);

                        Integer id = obj.getInt("Id");

                        if (placeId.equals(id)) {

                            result = new SkyscannerPlaceData();
                            result.setCode(obj.getString("Code"));
                            result.setName(obj.getString("Name"));
                            skyscannerPlaceData.put(id, result);

                            return result;
                        }
                    }
                }
            }
        }

        return null;
    }

    private JSONArray getPlaces() {
        return places;
    }

    private void setPlaces(JSONArray places) {
        this.places = places;
    }

    private JSONArray getCarriers() {
        return carriers;
    }

    private void setCarriers(JSONArray carriers) {
        this.carriers = carriers;
    }
}
