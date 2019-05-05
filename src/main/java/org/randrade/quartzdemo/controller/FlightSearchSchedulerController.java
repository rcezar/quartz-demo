package org.randrade.quartzdemo.controller;

import org.quartz.Scheduler;
import org.randrade.quartzdemo.entity.FlightDetailsEntity;
import org.randrade.quartzdemo.entity.FlightScheduleEntity;
import org.randrade.quartzdemo.entity.PriceRateEntity;
import org.randrade.quartzdemo.model.Error;
import org.randrade.quartzdemo.model.FlightSearchRequest;
import org.randrade.quartzdemo.repository.FlightDetailsRepository;
import org.randrade.quartzdemo.repository.FlightScheduleRepository;
import org.randrade.quartzdemo.repository.PriceRateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;

@RestController
public class FlightSearchSchedulerController {

    private static final Logger logger = LoggerFactory.getLogger(FlightSearchSchedulerController.class);

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private FlightScheduleRepository repository;

    @Autowired
    private FlightDetailsRepository detailsRepository;

    @PostMapping("/scheduleFlightSearch")
    public ResponseEntity scheduleEmail(@Valid @RequestBody FlightSearchRequest request) {

        logger.info("Schedule Flight Search");

        Error error = validateRequest(request);

        if (error != null) {

            return ResponseEntity.badRequest().body(error);
        }

        PriceRateEntity price = new PriceRateEntity();
        price.setPrice(BigDecimal.valueOf(132l));

        FlightScheduleEntity entity = new FlightScheduleEntity();
        entity.setFlexibleDate(false);
        entity.setInbound(request.getOriginPlace());
        entity.setOutbound(request.getDestinationPlace());
        entity.setInboundDate(Date.valueOf(request.getInboundDate()));
        entity.setOutboundDate(Date.valueOf(request.getOutboundDate()));
        entity.getPriceRates().add(price);

        repository.save(entity);


        FlightDetailsEntity detailsEntity = new FlightDetailsEntity();
        detailsEntity.setCompany("LATAM");
        detailsEntity.setFlightNumber("ABC123");
        detailsEntity.getPriceRates().add(price);

        detailsRepository.save(detailsEntity);

        return ResponseEntity.ok().build();

    }

    private Error validateRequest(FlightSearchRequest request) {

        if (request.getInboundDate().isBefore(LocalDate.now())) {
            return new Error("1000", "Inbound should be after now");
        }

        if (request.getOutboundDate().isBefore(request.getInboundDate())) {
            return new Error("1001", "Outbound date should be before Inbound date");
        }

        return null;

    }
}