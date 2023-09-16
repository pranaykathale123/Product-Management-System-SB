package com.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.stereotype.Repository;

import com.product.entity.User;

@EnableJpaRepositories
public interface userRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);

}
