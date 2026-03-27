package com.example.hrm_payroll.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hrm_payroll.Entity.Country;

@Repository
public interface CountryRepositry extends JpaRepository<Country,Integer> {
    
}
