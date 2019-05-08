package org.randrade.quartzdemo.service.impl;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.randrade.quartzdemo.payload.*;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;


public class SkyScannerServiceTestTest {

    private JSONArray places;
    private Map<Integer, SkyscannerPlaceData> skyscannerPlaceData;
    private JSONArray carriers;
    private Map<Integer, SkyscannerCarrierData> skyscannerCarrierData;


    @Test
    public void test() throws UnirestException {


        String url = "https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/pricing/v1.0";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();

        MultiValueMap<String, String> mapH= new LinkedMultiValueMap<String, String>();
        mapH.add("X-RapidAPI-Host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com");
        mapH.add("X-RapidAPI-Key", "48e90d683emsh832f24b745fa793p1ea4e7jsnf4381c3f6d91");
        mapH.add("Content-Type", "application/x-www-form-urlencoded");
        //mapH.add("User-Agent", "insomnia/6.4.2");


        headers.addAll(mapH);


        //headers.add("Content-Type", "application/x-www-form-urlencoded");
        //headers.add("X-RapidAPI-Host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com");
        //headers.add("X-RapidAPI-Key", "48e90d683emsh832f24b745fa793p1ea4e7jsnf4381c3f6d91");


        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        //map.add("Content-Type", "application/json");
        map.add("country", "US");
        map.add("currency", "USD");
        map.add("locale", "en-US");
        map.add("originPlace", "SFO-sky");
        map.add("destinationPlace", "LHR-sky");
        map.add("inboundDate", "2019-09-01");
        map.add("outboundDate", "2019-09-02");
        map.add("adults", "1");


        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);






        ResponseEntity<String> response = restTemplate
                .exchange(url, HttpMethod.POST, request, String.class);






        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        String foo = response.getBody();
        assertThat(foo, notNullValue());
    }

    @Test
    public void testUnirest() throws UnirestException {



        String url = "https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/pricing/v1.0";
        String rapidApiHost = "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com";
        String rapidApiKey = "48e90d683emsh832f24b745fa793p1ea4e7jsnf4381c3f6d91";


        HttpResponse<JsonNode> jsonResponse = Unirest.post(url)
                .header("X-RapidAPI-Host", rapidApiHost)
                .header("X-RapidAPI-Key", rapidApiKey)
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


        String session = null;

        if (HttpStatus.CREATED.value() == jsonResponse.getStatus()) {

            List<String> locationList = jsonResponse.getHeaders().get("Location");

            String[] l  = locationList.get(0).split("/");;

            session = l[l.length-1];
        }

        assertThat(jsonResponse.getStatus(), is(HttpStatus.CREATED.value()));



        String url2 = "https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/pricing/uk2/v1.0/";
        HttpResponse<JsonNode> jsonResponse2 = Unirest.get(url2 + session )
                .header("X-RapidAPI-Host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com")
                .header("X-RapidAPI-Key", "48e90d683emsh832f24b745fa793p1ea4e7jsnf4381c3f6d91")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("accept", "application/json")
                .queryString("sortType", "price")
                .queryString("pageIndex", "0")
                .queryString("pageSize", "10")
                .asJson();


        List<SkyScannerData> list = new ArrayList<>();

        JsonNode body = jsonResponse2.getBody();
        skyscannerPlaceData = new HashMap<>();
        skyscannerCarrierData = new HashMap<>();

        setPlaces((JSONArray) body.getObject().get("Places"));
        setCarriers((JSONArray) body.getObject().get("Carriers"));

        JSONArray legs = (JSONArray) body.getObject().get("Legs");

        JSONArray itineraries = (JSONArray) body.getObject().get("Itineraries");

        for (int i=0; i < itineraries.length(); i++) {

            SkyScannerData d = new SkyScannerData();
            //d.setInboundLeg(new SkyscannerLegData());
            //d.setOutboundLeg(new SkyscannerLegData());

            JSONObject in = itineraries.getJSONObject(i);

            d.setInboundLeg(getLegById(in.getString("InboundLegId"), legs));
            d.setOutboundLeg(getLegById(in.getString("OutboundLegId"), legs));

            //d.getOutboundLeg().setId(in.getString("OutboundLegId"));
            //d.getInboundLeg().setId(in.getString("InboundLegId"));

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



        JSONArray carriers = (JSONArray) body.getObject().get("Carriers");
        JSONArray agents = (JSONArray) body.getObject().get("Agents");


    }

    public JSONArray getPlaces() {
        return places;
    }

    public void setPlaces(JSONArray places) {
        this.places = places;
    }

    public JSONArray getCarriers() {
        return carriers;
    }

    public void setCarriers(JSONArray carriers) {
        this.carriers = carriers;
    }

    private SkyscannerLegData getLegById(String legId, JSONArray legs) {


        for (int i=0; i < legs.length(); i++) {

            JSONObject obj = legs.getJSONObject(i);

            if (legId.equalsIgnoreCase(obj.getString("Id"))){
                SkyscannerLegData leg = new SkyscannerLegData();

                leg.setId(legId);
                leg.setArrival(obj.getString("Arrival"));
                leg.setDeparture(obj.getString("Departure"));
                leg.setDuration(obj.getInt("Duration"));

                leg.setDestinationStation(getPlacesByID(obj.getInt("DestinationStation")));
                leg.setOriginStation(getPlacesByID(obj.getInt("OriginStation")));



                JSONArray carriers = obj.getJSONArray("Carriers");
                for (int t=0; t < carriers.length(); t++) {
                    leg.getCarriers().add(getCarriersByID(carriers.getInt(t)));
                }

                JSONArray flight = obj.getJSONArray("FlightNumbers");
                for (int j=0; j < flight.length(); j++) {

                    JSONObject objFlight = flight .getJSONObject(j);
                    leg.getFlightNumber().add(objFlight.getString("FlightNumber"));
                }

                return leg;
            }

        }

        return null;
    }

    private SkyscannerCarrierData getCarriersByID(Integer carrierId) {

        SkyscannerCarrierData result = skyscannerCarrierData.get(carrierId);

        if (result == null){

            JSONArray carriers = getCarriers();

            for (int j=0; j < carriers.length(); j++) {

                JSONObject obj = carriers.getJSONObject(j);

                Integer id = obj.getInt("Id");

                if (carrierId.equals(id)){

                    result = new SkyscannerCarrierData();
                    result.setCode(obj.getString("Code"));
                    result.setName(obj.getString("Name"));
                    skyscannerCarrierData.put(id, result);

                    return result;
                }
            }
        }

        return result;
    }

    private SkyscannerPlaceData getPlacesByID(Integer placeId) {

        SkyscannerPlaceData result = skyscannerPlaceData.get(placeId);

        if (result == null){

            JSONArray places = getPlaces();
            for (int j=0; j < places.length(); j++) {

                JSONObject obj = places.getJSONObject(j);

                Integer id = obj.getInt("Id");

                if (placeId.equals(id)){

                    result = new SkyscannerPlaceData();
                    result.setCode(obj.getString("Code"));
                    result.setName(obj.getString("Name"));
                    skyscannerPlaceData.put(id, result);

                    return result;
                }
            }
        }

        return result;
    }
}
