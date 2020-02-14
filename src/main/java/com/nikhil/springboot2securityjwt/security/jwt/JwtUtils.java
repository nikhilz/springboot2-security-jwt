package com.nikhil.springboot2securityjwt.security.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.nikhil.springboot2securityjwt.security.services.UserDetailsImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

/**  @author VEN01935
 * 
 * This class has 3 funtions:
 *  - generate a JWT from username, date, expiration, secret
 *  - get username from JWT
 *  - validate a JWT
 *
 */

@Component
public class JwtUtils {

	private static final Logger  logger = LoggerFactory.getLogger(JwtUtils.class);
	
	@Value("${nikhil.app.jwtSecret}")
	private String jwtSecret;
	
	@Value("${nikhil.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	
	/**
	 * 
	 * 
	 * If the authentication process is successful, we can get User’s information such as 
	 * username, password, authorities from an Authentication object.

		Authentication authentication = 
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		// userDetails.getUsername()
		// userDetails.getPassword()
		// userDetails.getAuthorities()
	 * 
	 * 
	 */
	
	public String generateJwtToken(Authentication authentication) {
		
		UserDetailsImpl detailsImpl = (UserDetailsImpl) authentication.getPrincipal();
		
		return Jwts.builder()
				.setSubject(detailsImpl.getUsername())
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime()+ jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}
	
	public String getUsernamefromJwtToken(String Token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(Token).getBody().getSubject();
	}

	
	public boolean validateJwt(String jwt) {
		try {
		Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt);
		return true;
		}catch (SignatureException e) {
			logger.error("Invalid JWT Signature : {} "+e.getMessage());
		}catch (MalformedJwtException e) {
			logger.error("Malformed JWT Token : {} "+e.getMessage());
		}catch (ExpiredJwtException e) {
			logger.error("JWT Token is Expired : {} "+e.getMessage());
		}catch (UnsupportedJwtException e) {
			logger.error("JWT Token is unsupported : {} "+e.getMessage());
		}catch (IllegalArgumentException e) {
			logger.error("JWT Token is unsupported : {} "+e.getMessage());
		}
		
		
		return false;
	}
}
