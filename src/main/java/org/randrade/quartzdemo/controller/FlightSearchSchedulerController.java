package org.randrade.quartzdemo.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.quartz.*;
import org.randrade.quartzdemo.entity.FlightScheduleEntity;
import org.randrade.quartzdemo.job.QueryFlightResultsJob;
import org.randrade.quartzdemo.model.Error;
import org.randrade.quartzdemo.model.FlightSearchRequest;
import org.randrade.quartzdemo.service.SkyScannerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;

import static org.quartz.DateBuilder.dateOf;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@RestController
public class FlightSearchSchedulerController {

    private static final Logger logger = LoggerFactory.getLogger(FlightSearchSchedulerController.class);

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SkyScannerService service;


    @PostMapping("/scheduleFlightSearch")
    public ResponseEntity scheduleEmail(@Valid @RequestBody FlightSearchRequest request) {

        logger.info("Schedule Flight Search");

        Error error = validateRequest(request);

        if (error != null) {

            return ResponseEntity.badRequest().body(error);
        }

        try {
            FlightScheduleEntity entity = service.createSession(request);

            if (entity != null && entity.getId() != null) {


                JobDetail jobDetail3AM =  buildJobDetail(entity.getId());
                Trigger trigger3AM = buildJobTrigger(jobDetail3AM, 3);

                JobDetail jobDetail9AM =  buildJobDetail(entity.getId());
                Trigger trigger9AM = buildJobTrigger(jobDetail9AM, 9);

                JobDetail jobDetail4PM =  buildJobDetail(entity.getId());
                Trigger trigger4PM = buildJobTrigger(jobDetail4PM, 16);

                JobDetail jobDetail8PM =  buildJobDetail(entity.getId());
                Trigger trigger8PM = buildJobTrigger(jobDetail8PM, 20);

                scheduler.scheduleJob(jobDetail3AM, trigger3AM);
                scheduler.scheduleJob(jobDetail9AM, trigger9AM);
                scheduler.scheduleJob(jobDetail4PM, trigger4PM);
                scheduler.scheduleJob(jobDetail8PM, trigger8PM);

                return ResponseEntity.ok().build();

            }

        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    private JobDetail buildJobDetail(Integer flightID) {

        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("flightID", flightID);

        return JobBuilder.newJob(QueryFlightResultsJob.class)
                .withIdentity(UUID.randomUUID().toString(), "flight-session-jobs")
                .withDescription("Query Session results job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, int hour) {

        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "flight-session-triggers-" + hour)
                .withDescription("Query Session results Trigger for hour " + hour)
                .withSchedule(simpleSchedule()
                        .withIntervalInHours(24)
                        .withMisfireHandlingInstructionFireNow()
                        .repeatForever())
                .startAt(dateOf(hour,0,0))
                .build();
    }


    private Error validateRequest(FlightSearchRequest request) {

        if (request.getInboundDate().isBefore(LocalDate.now())) {
            return new Error("1000", "Inbound should be after now");
        }

        if (request.getInboundDate().isBefore(request.getOutboundDate())) {
            return new Error("1001", "Outbound date should be before Inbound date");
        }

        return null;

    }
}