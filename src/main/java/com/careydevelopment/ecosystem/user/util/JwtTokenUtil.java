package com.careydevelopment.ecosystem.user.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.careydevelopment.ecosystem.user.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil {

	private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;
	
    @Value("${jwt.secret}")
    private String jwtSecret;

		
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(Claims::getSubject, token);
	}

	
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(Claims::getExpiration, token);
	}

	
	public <T> T getClaimFromToken(Function<Claims, T> claimsResolver, String token) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}
	
	
	public String getClaimFromTokenByName(String name, String token) {
		final Claims claims = getAllClaimsFromToken(token);
		return (String)claims.get(name);
	}
	
	
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
	}

	
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	
	public String generateToken(User user) {
		Map<String, Object> claims = addClaims(user); 
		return doGenerateToken(claims, user.getUsername());
	}

	
	private Map<String, Object> addClaims(User user) {
		Map<String, Object> claims = new HashMap<String, Object>();
		
		claims.put("id", user.getId());
		claims.put("authorities", user.getAuthorityNames());
		
		return claims;
	}	

	
	private String doGenerateToken(Map<String, Object> claims, String subject) {
		String token = Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
		
		return token;
	}

	
	public Boolean validateToken(UserDetails userDetails, String token) {
		final String username = getUsernameFromToken(token);
		return (userDetails != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	
	
	public Boolean validateToken(String token) {
		return (!isTokenExpired(token));
	}
	
	//no return necessary as this will throw an exception if there's a problem
	public void validateTokenWithSignature(String token) {
	    Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
	}
}
