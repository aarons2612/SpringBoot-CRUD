package com.postgresql.demo.activities;

import org.springframework.stereotype.Service;
import com.postgresql.demo.model.Demo;
import com.postgresql.demo.repo.DemoRepo;

@Service
public class PersonActivitiesImpl implements PersonActivities {

    private final DemoRepo repo;

    // ✅ Constructor-based injection
    public PersonActivitiesImpl(DemoRepo repo) {
        this.repo = repo;
    }

    @Override
    public Demo addPerson(Demo person) {
        System.out.println("Adding person to database: " + person.getName());

        // ✅ Check if email already exists before saving
        if (repo.existsByEmail(person.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        return repo.save(person);
    }


    @Override
    public void validatePerson(Long personId) {
        System.out.println("Validating person: " + personId);
        if (!repo.existsById(personId)) {
            throw new RuntimeException("Person not found!");
        }
    }

    @Override
    public void processPersonData(Long personId) {
        Demo person = repo.findById(personId).orElseThrow();
        System.out.println("Processing person data: " + person.getName());
    }

    @Override
    public void sendNotification(Long personId) {
        System.out.println("Sending notification for person ID: " + personId);
    }
}

