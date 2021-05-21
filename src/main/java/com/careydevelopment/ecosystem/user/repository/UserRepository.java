package com.careydevelopment.ecosystem.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.careydevelopment.ecosystem.user.model.User;

import us.careydevelopment.ecosystem.jwt.repository.UserDetailsRepository;

@Repository
public interface UserRepository extends MongoRepository<User, String>, UserDetailsRepository {

	public User findByUsername(String username);
	
	public User findByEmail(String email);
	
}
