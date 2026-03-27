package com.example.hrm_payroll.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hrm_payroll.Repository.CountryRepositry;
import com.example.hrm_payroll.Entity.Country;

import java.util.*;

@Service
public class CountryService {
    
    @Autowired
    CountryRepositry countryrep;

    public List<Country> getAllCountry(){
        return countryrep.findAll();
    }

    public Country getCountryById(Integer id){
        return countryrep.findById(id).get();
    }

    public Country getCountryByName(String name){
        List<Country> countries = countryrep.findAll();
        Country country = null;

        for(Country c:countries){

            if (c.getName().equalsIgnoreCase(name)) {
                country = c;
            }
        }

        return country;
    }

    public Country addCountry(Country country){
        //country.setId(getMaxId());
        return countryrep.save(country); 
    }


    public Country updateCountry(Country country){
        return countryrep.save(country);
    }

    
    public Country deleteCountry(Integer id){
        
        Country country = countryrep.findById(id).get();
        countryrep.deleteById(id);
        return country;
    }
}
