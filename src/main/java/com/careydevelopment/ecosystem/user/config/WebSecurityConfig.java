package com.careydevelopment.ecosystem.user.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.careydevelopment.ecosystem.user.util.ResponseWriterUtil;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtAuthenticationProvider jwtAuthenticationProvider;

	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	    auth.authenticationProvider(jwtAuthenticationProvider);
		auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
	}

	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
    
	
	//TODO: Harden security before going to production
	@Bean
	public CorsFilter corsFilter() {
    	UrlBasedCorsConfigurationSource source = new 
    	UrlBasedCorsConfigurationSource();
    	CorsConfiguration config = new CorsConfiguration();
    	config.setAllowCredentials(true);
    	config.addAllowedOrigin("*");
    	config.addAllowedHeader("*");
    	config.addAllowedMethod("*");
    	source.registerCorsConfiguration("/**", config);
    	
    	return new CorsFilter(source);
	}
	
    
	private BearerTokenAuthenticationFilter bearerTokenAuthenticationFilter() throws Exception {
		BearerTokenAuthenticationFilter filter = new BearerTokenAuthenticationFilter(authenticationManager());
		filter.setAuthenticationFailureHandler(authenticationFailureHandler());

		return filter;
	}
	
	
	private CredentialsAuthenticationFilter credentialsAuthenticationFilter() throws Exception {
		CredentialsAuthenticationFilter filter = new CredentialsAuthenticationFilter(authenticationManager());
		filter.setAuthenticationFailureHandler(authenticationFailureHandler());

		return filter;
	}
	
	
	private AuthenticationFailureHandler authenticationFailureHandler() {
		return (request, response, ex) -> {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			ResponseWriterUtil.writeErrorResponse(response, ex.getMessage());			
		};
	}
	
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {		
		httpSecurity
		    .cors().and()
		    .csrf().disable()
		    .addFilter(bearerTokenAuthenticationFilter())
		    .addFilter(credentialsAuthenticationFilter())
		    .authorizeRequests()
            .anyRequest().access("hasAuthority('CAREYDEVELOPMENT_CRM_USER')").and()
		    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}	
}
