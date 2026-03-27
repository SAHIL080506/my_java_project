package com.example.hrm_payroll.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.hrm_payroll.Services.CountryService;
import com.example.hrm_payroll.Entity.Country;
import java.util.List;


@RestController
@RequestMapping("/main")
public class FirstController {

    @Autowired
    CountryService service;
    
    @GetMapping("/get")
    public String getUser(@RequestParam(value="user") int usersNo) {
        if (usersNo == 1) {
            return "this is a get request for user 1";
        } 
        if (usersNo == 2) {
            return "this is a get request for user 2";
        }
        else {
            return "this is a get request for all users";
        }
    }

    //@RequestParam is specifically used for extracting specific conditioned value from url
    // whereas @PathVariable is used for extracting the whole variable value from url or 
    // specifically telling to go to that path/page
    @GetMapping(path="/{user_id}")
    public String getSingleUser(@PathVariable String user_id) {
        return "this is a get request for user id: "+user_id;
    }

    @PostMapping
    public String postUser() {
        return "this is a post request";
    }
    
    @PutMapping
    public String putUser() {
        return "this is a put request hi from sahil";
    }

    @DeleteMapping
    public String deleteUser() {
        return "this is a delete request";
    }

    @GetMapping("/getcountries")
    public List<Country> getAllCountries(){
        return service.getAllCountry();
    }

    @GetMapping("/getcountries/id/{id}")
    public ResponseEntity<Country> getCountryById(@PathVariable(value = "id") Integer id){
        
        try {
            Country country = service.getCountryById(id);
            return new ResponseEntity<Country>(country,HttpStatus.OK);
        } catch (Exception e) {
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/getcountries/name/{name}")
    public ResponseEntity<Country> getCountryByName(@PathVariable(value = "name") String name){
        
        try {
            Country country = service.getCountryByName(name);
            return new ResponseEntity<Country>(country,HttpStatus.OK);
        } catch (Exception e) {
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/addcountry")
    // public Country addCountry(@RequestBody Country country){
    //     return service.addCountry(country);
    // }
    public ResponseEntity<Country> addCountry(@RequestBody Country country){
        try {
            Country c = service.addCountry(country);
            return new ResponseEntity<Country>(c,HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/updatecountry/{id}")
    public Country updateCountry(@RequestBody Country country,@PathVariable Integer id){
        Country exist = service.getCountryById(id);
        exist.setName(country.getName());
        exist.setCapital(country.getCapital());

        return service.updateCountry(exist);
    }

    @DeleteMapping("/deletecountry/{id}")
    public Country deleteCountry(@PathVariable Integer id){
        return service.deleteCountry(id);
    }

}
