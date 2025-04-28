package com.ntt.userapi.repository;

import com.ntt.userapi.model.entity.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {
    // Basic repository needed for completeness, although cascade save handles phone creation
}