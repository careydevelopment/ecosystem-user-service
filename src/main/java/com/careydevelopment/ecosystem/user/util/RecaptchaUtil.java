package com.careydevelopment.ecosystem.user.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.careydevelopment.ecosystem.user.controller.RegistrationController;
import com.google.cloud.recaptchaenterprise.v1.RecaptchaEnterpriseServiceClient;
import com.google.recaptchaenterprise.v1.Assessment;
import com.google.recaptchaenterprise.v1.CreateAssessmentRequest;
import com.google.recaptchaenterprise.v1.Event;
import com.google.recaptchaenterprise.v1.ProjectName;
import com.google.recaptchaenterprise.v1.RiskAnalysis.ClassificationReason;

@Component
public class RecaptchaUtil {
    
    private static final Logger LOG = LoggerFactory.getLogger(RecaptchaUtil.class);

    private static final String ACTION = "submit";
    
    @Value("${recaptcha.project.id}")
    private String projectID;
    
    @Value("${recaptcha.site.key}")
    private String siteKey;
    
    
    public float createAssessment(String token) throws IOException {
        float recaptchaScore = 0f;
        
        // Initialize a client that will be used to send requests. This client needs to be created only
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the `client.close()` method on the client to safely
        // clean up any remaining background resources.
        try (RecaptchaEnterpriseServiceClient client = RecaptchaEnterpriseServiceClient.create()) {
            // Specify a name for this assessment.
            String assessmentName = "assessment-name";

            // Set the properties of the event to be tracked.
            Event event = Event.newBuilder()
                                  .setSiteKey(siteKey)
                                  .setToken(token)
                                  .build();

            // Build the assessment request.
            CreateAssessmentRequest createAssessmentRequest = CreateAssessmentRequest.newBuilder()
                                                                .setParent(ProjectName.of(projectID).toString())
                                                                .setAssessment(Assessment.newBuilder().setEvent(event).setName(assessmentName).build())
                                                                .build();

            Assessment response = client.createAssessment(createAssessmentRequest);

            // Check if the token is valid.
            if (!response.getTokenProperties().getValid()) {
                LOG.error("The CreateAssessment call failed because the token was: " +
                          response.getTokenProperties().getInvalidReason().name());
                return recaptchaScore;
            }

            // Check if the expected action was executed.
            if (!response.getTokenProperties().getAction().equals(ACTION)) {
                LOG.error("The action attribute in your reCAPTCHA tag " +
                          "does not match the action you are expecting to score");
                return recaptchaScore;
            }

            // Get the risk score and the reason(s).
            // For more information on interpreting the assessment,
            // see: https://cloud.google.com/recaptcha-enterprise/docs/interpret-assessment
            recaptchaScore = response.getRiskAnalysis().getScore();
            LOG.debug("The reCAPTCHA score is: " + recaptchaScore);

            for (ClassificationReason reason : response.getRiskAnalysis().getReasonsList()) {
                LOG.debug("Reason is " + reason);
            }
        }
        
        return recaptchaScore;
    }
    
}
