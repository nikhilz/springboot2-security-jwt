package com.nikhil.springboot2securityjwt.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nikhil.springboot2securityjwt.security.services.UserDetailsServiceImpl;

/**
 *  a filter that executes once per request
 * @author VEN01935
 * 
 * 
 *
 */

public class AuthTokenFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
	
	/**
	 * 
	 * What we do inside doFilterInternal():
		– get JWT from the Authorization header (by removing Bearer prefix)
		– if the request has JWT, validate it, parse username from it
		– from username, get UserDetails to create an Authentication object
		– set the current UserDetails in SecurityContext using setAuthentication(authentication) method.
	 * 
	 * After this, everytime you want to get UserDetails, just use SecurityContext like this:

	UserDetails userDetails =
	(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

	 *	// userDetails.getUsername()
	 *	// userDetails.getPassword()
	 *	// userDetails.getAuthorities()
	*/
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
		String jwt = parseJwt(request);
		
		if(jwt!=null && jwtUtils.validateJwt(jwt)) {
			String username = jwtUtils.getUsernamefromJwtToken(jwt);
			
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		}catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e);
		}
	}
	
	private String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");
		if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7, headerAuth.length());
		}
		return null;
		
	}

}
