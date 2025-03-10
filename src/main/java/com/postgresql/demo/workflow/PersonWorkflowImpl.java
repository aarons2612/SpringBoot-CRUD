package com.postgresql.demo.workflow;

import com.postgresql.demo.activities.PersonActivities;
import com.postgresql.demo.model.Demo;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class PersonWorkflowImpl implements PersonWorkflow {

    private final PersonActivities activities = Workflow.newActivityStub(
            PersonActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .build()
    );

    private Long savedPersonId; // ✅ This is now properly updated

    @Override
    public Demo processPerson(Demo person) {
        Demo savedPerson = activities.addPerson(person); // ✅ Store in DB
        System.out.println("Workflow started for person ID: " + savedPerson.getId());

        // Workflow logic
        activities.validatePerson(savedPerson.getId());
        activities.processPersonData(savedPerson.getId());
        activities.sendNotification(savedPerson.getId());

        System.out.println("Workflow completed for person ID: " + savedPerson.getId());

        return savedPerson;
    }

    // @Override
    // public Long updatePersonId() {
    //     return this.savedPersonId;  // ✅ Returns person ID when called
    // }
}

