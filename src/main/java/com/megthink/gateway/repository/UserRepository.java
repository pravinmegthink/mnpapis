package com.megthink.gateway.repository;

import com.megthink.gateway.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Integer> {
    //User findByUsername(String username);
    User findByUserId(int userId);    
    Optional<User> findByUsername(String username);
}
