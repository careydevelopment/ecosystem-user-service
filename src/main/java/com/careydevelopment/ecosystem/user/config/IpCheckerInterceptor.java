package com.careydevelopment.ecosystem.user.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Going this route instead of CORS because CORS doesn't support wildcarding of ports
 * 
 * Need wildcarded ports for service-to-service calls because Spring Boot uses different ports
 * with every call
 */
public class IpCheckerInterceptor implements HandlerInterceptor {

    private static final String ALLOWED_ORIGIN = "https://bixis.us";
    
    private static final Logger LOG = LoggerFactory.getLogger(IpCheckerInterceptor.class);
   
    private List<String> ipWhitelist = new ArrayList<>();
    private String privateIp = "0.0.0.6";
    
    public IpCheckerInterceptor(String[] ipWhitelist, String privateIp) {
        this.ipWhitelist = List.of(ipWhitelist);
        this.privateIp = privateIp;
    }
       
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //anything from application domain is allowed
        String header = request.getHeader(HttpHeaders.ORIGIN);
        if (ALLOWED_ORIGIN.equals(header)) {
            return true;
        }
        
        String ipAddress = request.getRemoteAddr();
        LOG.debug("Remote IP address is " + ipAddress);
        
        if (ipAddress != null) {
            //necessary for pod-to-pod communication
            if (ipAddress.startsWith(privateIp)) {
                return true;
            }
            
            if (ipWhitelist.contains(ipAddress)) {
                return true;                
            } else {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "You're not authorized to access this resource.");
                return false;
            }
        } else {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "You're not authorized to access this resource.");
            return false;
        }
    }
 }
