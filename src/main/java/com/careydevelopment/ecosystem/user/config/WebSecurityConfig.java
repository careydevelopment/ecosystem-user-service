package com.careydevelopment.ecosystem.user.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

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
	
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
		    .cors().and()
		    .csrf().disable()
		    .addFilter(new BearerTokenAuthenticationFilter(authenticationManager()))
		    .addFilter(new CredentialsAuthenticationFilter(authenticationManager()))
		    .authorizeRequests() 
            .anyRequest().access("hasAuthority('CAREYDEVELOPMENT_CRM_USER')").and()
		    .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
		    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
}
