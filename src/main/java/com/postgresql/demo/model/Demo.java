package com.postgresql.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "demo")
public class Demo {
    
    @Id
    @SequenceGenerator(name = "custom_seq", sequenceName = "custom_sequence", initialValue = 1000, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "custom_seq")
    private Long id;

    private String name;
    @Column(unique = true)
    private String email;

    // public Demo() {}

    // public Demo(String name, String email) {
    //     this.name = name;
    //     this.email = email;
    // }

    // public Long getId() {
    //     return id;
    // }

    // public void setId(Long id) {
    //     this.id = id;
    // }

    // public String getName() {
    //     return name;
    // }

    // public void setName(String name) {
    //     this.name = name;
    // }

    // public String getEmail() {
    //     return email;
    // }

    // public void setEmail(String email){
    //     this.email = email;
    // }
}
