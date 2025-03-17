package com.postgresql.demo.workflow;

import com.postgresql.demo.activities.PersonActivities;
import com.postgresql.demo.model.Demo;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class PersonWorkflowImpl implements PersonWorkflow {

    private final PersonActivities activities = Workflow.newActivityStub(
            PersonActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .build()
    );

    // private Long savedPersonId; // ✅ This is now properly updated

public Demo processPerson(Demo person) {
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // System.out.println("\nBefore workflow time:  " + now.format(formatter));
    Demo savedPerson = activities.addPerson(person); // ✅ Store in DB

    System.out.println("Workflow started for person ID: " + savedPerson.getId());
    System.out.println("\nAfter workflow time: " + now.format(formatter));

    // Workflow logic
    activities.validatePerson(savedPerson.getId());
    activities.processPersonData(savedPerson.getId());
    activities.sendNotification(savedPerson.getId());

    System.out.println("Workflow completed for person ID: " + savedPerson.getId());
    System.out.println("\nreturn time: " + now.format(formatter));

    return savedPerson;
}


    // @Override
    // public Long updatePersonId() {
    //     return this.savedPersonId;  // ✅ Returns person ID when called
    // }
}

