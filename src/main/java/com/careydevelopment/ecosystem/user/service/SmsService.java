package com.careydevelopment.ecosystem.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vonage.client.VonageClient;
import com.vonage.client.verify.CheckResponse;
import com.vonage.client.verify.VerifyResponse;
import com.vonage.client.verify.VerifyStatus;

/**
 * Using Vonage's verification service because if the user
 * doesn't verify, then we don't get charged for the SMS.
 * 
 * If we just send a text message and check the code like we
 * do with email, we get charged no matter what.
 * 
 */
@Service
public class SmsService {

    private static final Logger LOG = LoggerFactory.getLogger(SmsService.class);
    
    private static final String BRAND_NAME = "Carey Development";
    
    
    @Value("${vonage.api.key}")
    private String apiKey;
    
    @Value("${vonage.api.secret}")
    private String apiSecret;
    
    
    private VonageClient getClient() {
        VonageClient client = VonageClient
                .builder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .build();

        return client;
    }
    
    
    public void cancelRequest(String requestId) {
        VonageClient client = getClient();
        client.getVerifyClient().cancelVerification(requestId);
        LOG.debug("Verification cancelled.");
    }
    
    
    public String sendValidationCode(String phoneNumber) {
        LOG.debug("Sending validation code to " + phoneNumber);
        
        VonageClient client = getClient();
        
        VerifyResponse response = client
                                    .getVerifyClient()
                                    .verify(phoneNumber, BRAND_NAME);

        if (response.getStatus() == VerifyStatus.OK) {
            LOG.debug("Valid request: " + response.getRequestId());
            return response.getRequestId();
        } else {
            LOG.error("Problem sending SMS: " + response.getStatus() + " " + response.getErrorText());
            return null;
        }
    }
    
    
    public boolean checkValidationCode(String requestId, String code) {
        VonageClient client = getClient();
        
        CheckResponse response = client.getVerifyClient().check(requestId, code);

        if (response.getStatus() == VerifyStatus.OK) {
            LOG.debug("SMS Verification Successful");
            return true;
        } else {
            LOG.debug("Verification failed: " + response.getErrorText());
            return false;
        }
    }
}
