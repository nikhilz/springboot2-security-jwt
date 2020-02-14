package com.nikhil.springboot2securityjwt.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nikhil.springboot2securityjwt.pojo.requests.LoginRequest;
import com.nikhil.springboot2securityjwt.pojo.response.JwtResponse;
import com.nikhil.springboot2securityjwt.repositories.RoleRepository;
import com.nikhil.springboot2securityjwt.repositories.UserRepository;
import com.nikhil.springboot2securityjwt.security.jwt.JwtUtils;
import com.nikhil.springboot2securityjwt.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;
	
	
	/**
	 * 
	 * 
	 * – /api/auth/signin

		-- authenticate { username, pasword }
		-- update SecurityContext using Authentication object
		-- generate JWT
		-- get UserDetails from Authentication object
		-- response contains JWT and UserDetails data
	 * 
	 * 
	 */
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map((role) -> role.getAuthority()).collect(Collectors.toList());
		
		return ResponseEntity.ok( new JwtResponse(jwt, 
				userDetails.getId(), 
				userDetails.getUsername(), 
				userDetails.getEmail(), 
				roles));
	}
	
	/**
	 * 
	 *  * – /api/auth/signup
		-- check existing username/email
		-- create new User (with ROLE_USER if not specifying role)
		-- save User to database using UserRepository
	 * 
	 * 
	 */
	
}
