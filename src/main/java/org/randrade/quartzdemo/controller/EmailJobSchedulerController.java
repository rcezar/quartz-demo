package org.randrade.quartzdemo.controller;

import org.quartz.*;
import org.randrade.quartzdemo.job.EmailJob;
import org.randrade.quartzdemo.model.ScheduleEmailRequest;
import org.randrade.quartzdemo.model.ScheduleEmailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
public class EmailJobSchedulerController {

    private static final Logger logger = LoggerFactory.getLogger(EmailJobSchedulerController.class);

    @Autowired
    private Scheduler scheduler;

    @PostMapping("/scheduleEmail")
    public ResponseEntity<ScheduleEmailResponse> scheduleEmail(@Valid @RequestBody ScheduleEmailRequest scheduleEmailRequest){

        ZonedDateTime dateTime = ZonedDateTime.of(scheduleEmailRequest.getDateTime(), scheduleEmailRequest.getTimeZone());

        if(dateTime.isBefore(ZonedDateTime.now())) {
            ScheduleEmailResponse scheduleEmailResponse = new ScheduleEmailResponse(false,
                    "dateTime must be after current time");
            return ResponseEntity.badRequest().body(scheduleEmailResponse);
        }

        try{

            JobDetail jobDetail =  buildJobDetail(scheduleEmailRequest);
            Trigger trigger = buildJobTrigger(jobDetail, dateTime);

            scheduler.scheduleJob(jobDetail, trigger);


            ScheduleEmailResponse response = new ScheduleEmailResponse(
                    true,
                    jobDetail.getKey().getName(),
                    jobDetail.getKey().getGroup(),
                    "Email Scheduled Successfully!");

            return ResponseEntity.ok(response);

        } catch (SchedulerException ex) {

            logger.error("Error scheduling email", ex);

            ScheduleEmailResponse scheduleEmailResponse = new ScheduleEmailResponse(
                    false,
                    "Error scheduling email. Please try later!");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(scheduleEmailResponse);
        }
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime dateTime) {

        return TriggerBuilder.newTrigger()
                    .forJob(jobDetail)
                    .withIdentity(jobDetail.getKey().getName(), "email-triggers")
                    .withDescription("Send Email Trigger")
                    .startAt(Date.from(dateTime.toInstant()))
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                    .build();
    }

    private JobDetail buildJobDetail(ScheduleEmailRequest request) {

        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("email", request.getEmail());
        jobDataMap.put("subject", request.getSubject());
        jobDataMap.put("body", request.getBody());

        return JobBuilder   .newJob(EmailJob.class)
                            .withIdentity(UUID.randomUUID().toString(), "email-jobs")
                            .withDescription("Send email job")
                            .usingJobData(jobDataMap)
                            .storeDurably()
                            .build();
    }


}
