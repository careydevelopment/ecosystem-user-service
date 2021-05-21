package com.careydevelopment.ecosystem.user.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;

import com.careydevelopment.ecosystem.user.exception.UserServiceAuthenticationException;
import com.careydevelopment.ecosystem.user.service.JwtUserDetailsService;
import com.careydevelopment.ecosystem.user.util.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationProvider.class);

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(BearerTokenAuthenticationToken.class);
    }
    
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        BearerTokenAuthenticationToken bearerToken = (BearerTokenAuthenticationToken) authentication;
        Authentication auth = null;
        
        try {
            String token = bearerToken.getToken();
            
            //validate the token
            jwtTokenUtil.validateTokenWithSignature(token);
            
            String username = jwtTokenUtil.getUsernameFromToken(token);
            
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);

            auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());            
            LOG.debug("Authentication token: " + auth);            
        } catch (IllegalArgumentException e) {
            throw new UserServiceAuthenticationException("Invalid token");
        } catch (ExpiredJwtException e) {
            throw new UserServiceAuthenticationException("Token expired");
        } catch (SignatureException e) {
            throw new UserServiceAuthenticationException("Invalid signature");
        }
        
        return auth;
    }
}
