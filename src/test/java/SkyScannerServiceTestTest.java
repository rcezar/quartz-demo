import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;


public class SkyScannerServiceTestTest {


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


        List<SkyscannerData> list = new ArrayList<>();

        JsonNode body = jsonResponse2.getBody();
        JSONArray legs = (JSONArray) body.getObject().get("Legs");

        JSONArray itineraries = (JSONArray) body.getObject().get("Itineraries");

        for (int i=0; i < itineraries.length(); i++) {

            SkyscannerData d = new SkyscannerData();
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
        JSONArray places = (JSONArray) body.getObject().get("Places");



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

                leg.setDestinationStation(obj.getString("DestinationStation"));
                leg.setOriginStation(obj.getString("OriginStation"));

                leg.setCarriers(obj.getString("Carriers"));

                return leg;
            }
        }

        return null;
    }


    class SkyscannerData {

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
    }

    class SkyscannerLegData{

        private String id;
        private String originStation;
        private String destinationStation;
        private String departure;
        private String arrival;
        private int duration;
        private String carriers;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOriginStation() {
            return originStation;
        }

        public void setOriginStation(String originStation) {
            this.originStation = originStation;
        }

        public String getDestinationStation() {
            return destinationStation;
        }

        public void setDestinationStation(String destinationStation) {
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

        public String getCarriers() {
            return carriers;
        }

        public void setCarriers(String carriers) {
            this.carriers = carriers;
        }
    }

    class SkyscannerPricingOptionData {

        private String agent;
        private BigDecimal price;

        public String getAgent() {
            return agent;
        }

        public void setAgent(String agent) {
            this.agent = agent;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }


    /*
    HttpResponse<JsonNode> response = Unirest.post("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/pricing/v1.0")
.header("X-RapidAPI-Host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com")
.header
.header
.field("inboundDate", "2019-09-10")
.field("cabinClass", "business")
.field("children", 0)
.field("infants", 0)
.field("country", "US")
.field("currency", "USD")
.field("locale", "en-US")
.field("originPlace", "SFO-sky")
.field("destinationPlace", "LHR-sky")
.field("outboundDate", "2019-09-01")
.field("adults", 1)
.asJson();
     */
}