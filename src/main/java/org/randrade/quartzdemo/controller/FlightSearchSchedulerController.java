package org.randrade.quartzdemo.controller;

import org.quartz.Scheduler;
import org.randrade.quartzdemo.model.Error;
import org.randrade.quartzdemo.model.FlightSearchRequest;
import org.randrade.quartzdemo.model.ScheduleEmailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
public class FlightSearchSchedulerController {

    private static final Logger logger = LoggerFactory.getLogger(FlightSearchSchedulerController.class);

    @Autowired
    private Scheduler scheduler;

    @PostMapping("/scheduleFlightSearch")
    public ResponseEntity scheduleEmail(@Valid @RequestBody FlightSearchRequest request) {

        logger.info("Schedule Flight Search");

        Error error = validateRequest(request);

        if (error != null) {

            return ResponseEntity.badRequest().body(error);
        }

        return ResponseEntity.ok().build();

    }

    private Error validateRequest(FlightSearchRequest request) {

        if (request.getInboundDate().isBefore(LocalDate.now())) {
            return new Error("1000", "Inbound should be before now");
        }

        if (request.getOutboundDate().isBefore(request.getInboundDate())) {
            return new Error("1001", "Outbound date should be before Inbound date");
        }

        return null;

    }
}