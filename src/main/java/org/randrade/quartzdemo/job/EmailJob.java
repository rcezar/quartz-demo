package org.randrade.quartzdemo.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Component
public class EmailJob extends QuartzJobBean {

    private static final Logger logger = LoggerFactory.getLogger(EmailJob.class);
    
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailProperties mailProperties;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        logger.info("Executing Job {}", jobExecutionContext.getJobDetail().getKey().getName());

        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();

        sendMail(mailProperties.getUsername(),
                jobDataMap.getString("subject"),
                jobDataMap.getString("body"),
                jobDataMap.getString("email"));
    }

    private void sendMail(String username, String subject, String body, String email) {

        try {
            logger.info("Sending Email to {}", email);

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper messageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.toString());
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);
            messageHelper.setFrom(username);
            messageHelper.setTo(email);

            mailSender.send(message);

        } catch (MessagingException ex) {
            logger.error("Failed to send email to {}", email);
        }

    }
}
