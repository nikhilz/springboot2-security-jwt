package com.nikhil.springboot2securityjwt.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nikhil.springboot2securityjwt.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findUserByUsername(String Name);
	Boolean existsByUsername(String name);
	Boolean existsByEmail(String email);
	
}
