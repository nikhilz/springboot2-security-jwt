package com.nikhil.springboot2securityjwt.security.jwt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 
 * @author VEN01935
 *
 */


@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	
	private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);
	/**
	 * This method will be triggerd anytime unauthenticated User requests a secured HTTP resource 
	 * and an AuthenticationException is thrown.
	 * HttpServletResponse.SC_UNAUTHORIZED is the 401 Status code
	 */
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		logger.error("Unauthorized Error : {}"+ authException.getMessage());
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Error: Unauthorized");

	}

}
