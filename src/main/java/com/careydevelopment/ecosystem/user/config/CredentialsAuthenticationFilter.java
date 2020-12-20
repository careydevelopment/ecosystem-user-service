package com.careydevelopment.ecosystem.user.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.careydevelopment.ecosystem.user.controller.JwtAuthenticationController;
import com.careydevelopment.ecosystem.user.model.JwtRequest;
import com.careydevelopment.ecosystem.user.model.JwtResponse;
import com.careydevelopment.ecosystem.user.model.User;
import com.careydevelopment.ecosystem.user.util.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CredentialsAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    
    private static final Logger LOG = LoggerFactory.getLogger(CredentialsAuthenticationFilter.class);


    private AuthenticationManager authenticationManager;
    
    
    public CredentialsAuthenticationFilter(AuthenticationManager man) {
        this.authenticationManager = man;
        this.setFilterProcessesUrl("/authenticate");
    }
    
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        Authentication auth = null;
        
        try {
            JwtRequest jwtRequest = new ObjectMapper().readValue(req.getInputStream(), JwtRequest.class);
            LOG.debug("The request is " + jwtRequest);
            
            auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    jwtRequest.getUsername(), jwtRequest.getPassword()));
                        
        } catch (Exception e) {
            LOG.error("Problem logging in user with credentials!", e);
        }
        
        return auth;
    }
    
    
    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, 
            FilterChain chain, Authentication auth) throws IOException {
        
        final User user = (User)auth.getPrincipal();
        final JwtTokenUtil jwtTokenUtil = JwtTokenUtil.generateToken(user);
        final String token = jwtTokenUtil.getToken();
        Long expirationDate = jwtTokenUtil.getExpirationDateFromToken().getTime();

        JwtResponse jwtResponse = new JwtResponse(token, user, expirationDate);
        
        String body = new ObjectMapper().writeValueAsString(jwtResponse);
        LOG.debug("Body response is " + body);
        
        res.getWriter().write(body);
        res.getWriter().flush();
    }
}
