package org.randrade.quartzdemo.job;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.randrade.quartzdemo.entity.FlightDetailsEntity;
import org.randrade.quartzdemo.entity.FlightScheduleEntity;
import org.randrade.quartzdemo.entity.PriceRateEntity;
import org.randrade.quartzdemo.payload.SkyScannerData;
import org.randrade.quartzdemo.payload.SkyscannerPricingOptionData;
import org.randrade.quartzdemo.repository.FlightDetailsRepository;
import org.randrade.quartzdemo.repository.FlightScheduleRepository;
import org.randrade.quartzdemo.service.SkyScannerService;
import org.randrade.quartzdemo.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class QueryFlightResultsJob extends QuartzJobBean {

    private static final Logger logger = LoggerFactory.getLogger(QueryFlightResultsJob.class);

    @Autowired
    private SkyScannerService service;

    @Autowired
    private FlightScheduleRepository repository;


    @Autowired
    private FlightDetailsRepository detailsRepository;


    private BigDecimal lowestRating ;


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        logger.info("Executing Job {}", jobExecutionContext.getJobDetail().getKey().getName());

        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();

        Integer flightID = Integer.valueOf(jobDataMap.getString("flightID"));

        if (!StringUtils.isEmpty(flightID)) {

            FlightScheduleEntity entity = repository.getOne(flightID);

            if (entity != null && !StringUtils.isEmpty(entity.getSession())) {

                try {

                    List<SkyScannerData> results = service.getSessionResults(entity.getSession());

                    if (!CollectionUtils.isEmpty(results)) {

                        //recuperar FlightSchedule entity por session

                        lowestRating = new BigDecimal(Integer.MAX_VALUE);

                        entity.getPriceRates().addAll(convertPrices(results));

                        repository.save(entity);

                        if (lowestRating.compareTo(entity.getTargetRate()) > 0) {

                            //TODO: OK ACHOU... notificar de alguma forma e parar jobo
                            logger.info("Parabens, achou um rating menor: {}  target: {}", lowestRating, entity.getTargetRate());

                        }else {

                            logger.info("Did not find satisfactory results regarding price rates... ");
                            // validar se o mesmo session id retorna precos diferentes com o tempo..
                            // se sim, ok
                            // se nao, schedule job parar criar um novo session id;

                        }
                    }else{
                        logger.info("Service didnt query any results.... DO nothing..");
                    }

                } catch (UnirestException e) {
                    e.printStackTrace();
                }


            }else{
                logger.info("Session id is empty... Creating job to retrieve session id from Skyscanner API...");

            }
        }
    }


    private List<PriceRateEntity> convertPrices(List<SkyScannerData> sessionResults) {

        List<PriceRateEntity> priceList = new ArrayList<>();

        for (SkyScannerData s : sessionResults) {

            FlightDetailsEntity detailsEntity = new FlightDetailsEntity();

            detailsEntity.setCompany(s.getInboundLeg().getCarriers().get(0).getName());
            detailsEntity.setFlightNumber(s.getInboundLeg().getFlightNumber().get(0));
            try {
                detailsEntity.setFlightDate(DateUtils.convert(DateUtils.parseDate(s.getInboundLeg().getDeparture())));

            } catch (ParseException e) {

                logger.error("Error on parsing string date to Utils Date...");
            }

            for (SkyscannerPricingOptionData p : s.getPricingOptionDataList()){


                if (lowestRating.compareTo(p.getPrice()) > 0) {
                    lowestRating = p.getPrice();
                }

                PriceRateEntity price = new PriceRateEntity();
                price.setPrice(p.getPrice());

                detailsEntity.getPriceRates().add(price);
                priceList.add(price);

            }

            detailsRepository.save(detailsEntity);

        }

        return priceList;
    }


}