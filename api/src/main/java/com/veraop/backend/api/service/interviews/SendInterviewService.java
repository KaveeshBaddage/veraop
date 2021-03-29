package com.veraop.backend.api.service.interviews;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.veraop.backend.api.dto.OnboardRequestDataDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Date;

@Service
public class SendInterviewService {

        private Template template;

        private static final String EMAIL_TEXT_TEMPLATE = "Dear %s,\n" +
                "\n" +
                "Thank you for applying for the position of %s, \n Your interview has been scheduled for %s, " +
                "\n" +
                "\n" +
                "Please call me at 651-555-6666 or email me if you have any questions or need to reschedule." +
                "\n" +
                "\n" +
                "Best regards,\n" +
                "%s\n";

        @Value("${onboard.email.sender.address}")
        private String senderMailAddress;

        @Value("${company.name}")
        private String companyName;

        @Value("${onboard.email.sender.name}")
        private String senderName;

        @PostConstruct
        private void postProcess() {
            template = new Template();
            template.setTemplateName("OnboardInvitation");
            template.setSubjectPart("Welcome Onboard {{name}}!");
            template.setHtmlPart("<h1>Welcome to the place {{name}}</h1>");
            template.setTextPart("Sample text part");
        }

        /**
         * Sends the onboard welcome email to the given user.
         *
         * @param requestData data from onboard API request
         */
        public void sendInterviewInvitation(String candidateName, String candidateEmail, Date interviewTime) {

            String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            AmazonSimpleEmailService emailClient = AmazonSimpleEmailServiceClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1)
                    .build();

            Destination destination = new Destination(List.of(candidateEmail));

           String  emailContent =  String.format(EMAIL_TEXT_TEMPLATE, candidateName,companyName, interviewTime.toString(),
                    senderName);

           System.out.println("-----Interview Invitation Email Content--------"+emailContent);

            Message mailMessage = new Message(new Content(String.format("Welcome to %s", companyName)),
                    new Body(new Content(emailContent)));

            SendEmailRequest sendEmailRequest = new SendEmailRequest(senderMailAddress, destination, mailMessage);

            SendEmailResult sendEmailResult = emailClient.sendEmail(sendEmailRequest);
        }
}
