package com.spring.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.spring.Entity.User;
import java.util.List;


public interface UserRepo extends JpaRepository<User, Integer> {
	
	  Optional<User> findByEmail(String email);
	  
}
