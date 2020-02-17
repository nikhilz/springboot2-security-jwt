package com.nikhil.springboot2securityjwt.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.nikhil.springboot2securityjwt.models.ERole;
import com.nikhil.springboot2securityjwt.models.Role;
import com.nikhil.springboot2securityjwt.models.User;
import com.nikhil.springboot2securityjwt.pojo.requests.LoginRequest;
import com.nikhil.springboot2securityjwt.pojo.requests.SignupRequest;
import com.nikhil.springboot2securityjwt.pojo.response.JwtResponse;
import com.nikhil.springboot2securityjwt.pojo.response.MessageResponse;
import com.nikhil.springboot2securityjwt.repositories.RoleRepository;
import com.nikhil.springboot2securityjwt.repositories.UserRepository;
import com.nikhil.springboot2securityjwt.security.jwt.JwtUtils;
import com.nikhil.springboot2securityjwt.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	
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
		System.out.println("=====================");
		
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
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest request){
		if(userRepository.existsByUsername(request.getUsername())) {
			return  ResponseEntity.badRequest()
					.body(new MessageResponse("Error: Username already exists!"));
		}
		
		if(userRepository.existsByEmail(request.getEmail())) {
			return  ResponseEntity.badRequest()
					.body(new MessageResponse("Error: Email already in use!"));
		}
		
		// Create new user's account
		User user = new User(request.getUsername(),
				request.getEmail(),
				encoder.encode(request.getPassword()));
		
		Set<String> strRoles = request.getRole();
		Set<Role> roles = new HashSet<Role>();
		
		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
				.orElseThrow(() -> new RuntimeException("Error: Role not found!"));
			roles.add(userRole);
		}else {
			strRoles.forEach(role  -> {
				switch (role) {
				case "admin": 
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role not found!"));
					roles.add(adminRole);
					break;
					
				case "mod": 
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new RuntimeException("Error: Role not found!"));
					roles.add(modRole);
					break;

				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role not found!"));
					roles.add(userRole);
					break;
				}
			});
		}
		user.setRoles(roles);
		userRepository.save(user);
		
		return ResponseEntity.ok(new MessageResponse("User Registered Successfully!"));
		
	}
}
