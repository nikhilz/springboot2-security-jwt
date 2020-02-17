package com.nikhil.springboot2securityjwt.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nikhil.springboot2securityjwt.security.jwt.AuthTokenFilter;

/***
 * 
 * @author VEN01935
 *
 *There are 4 APIs:
	-– /api/test/all for public access
	-– /api/test/user for users has ROLE_USER or ROLE_MODERATOR or ROLE_ADMIN
	-– /api/test/mod for users has ROLE_MODERATOR
	-– /api/test/admin for users has ROLE_ADMIN
 *
 *
	we used @EnableGlobalMethodSecurity(prePostEnabled = true) for WebSecurityConfig class

	@Configuration
	@EnableWebSecurity
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	public class WebSecurityConfig extends WebSecurityConfigurerAdapter { ... }
	
	Now we can secure methods in our Apis with @PreAuthorize annotation easily.
 *
 */

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

	private static final Logger logger = LoggerFactory.getLogger(TestController.class);
	
	@GetMapping("/all")
	public String allAccess() {
		logger.warn("------------- all ------------");
		return "Public Content";
	}
	
	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public String UserAccess() {
		return "User Content";
	}
	
	@GetMapping("/mod")
	@PreAuthorize("hasRole('MODERATOR')")
	public String ModAccess() {
		return "Moderator Board";
	}
	
	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String AdminAccess() {
		return "Admin Board";
	}
}
