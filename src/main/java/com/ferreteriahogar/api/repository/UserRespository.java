package com.ferreteriahogar.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ferreteriahogar.api.model.User;

@Repository
public interface UserRespository extends JpaRepository<User, Long>{
    User findByUsername(String username);
}
