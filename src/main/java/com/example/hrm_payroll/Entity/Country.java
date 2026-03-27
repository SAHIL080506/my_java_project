package com.example.hrm_payroll.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="Country")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)// AUTO_INCREMENT
    @Column(name="id")
    Integer id;  // Integer instead of int to handle null values

    @Column(name="name")
    String name;

    @Column(name="capital")
    String capital;

    public Country() {}

    public Country(Integer id, String name, String capital) {
        this.id = id;
        this.name = name;
        this.capital = capital;
    }

    public int getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getCapital() { return capital; }

    public void setCapital(String capital) { this.capital = capital; }
}
