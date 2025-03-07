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

    private Long savedPersonId;

    @Override
    public void processPerson(Demo person) {
        Demo savedPerson = activities.addPerson(person); // ✅ Store in DB
        this.savedPersonId = savedPerson.getId(); // ✅ Assign ID before query

        System.out.println("Workflow started for person ID: " + savedPersonId);

        // Workflow logic
        activities.validatePerson(savedPersonId);
        activities.processPersonData(savedPersonId);
        activities.sendNotification(savedPersonId);

        System.out.println("Workflow completed for person ID: " + savedPersonId);
    }

    @Override
    public Long getPersonId() {
        while (savedPersonId == null) { // Wait for ID to be set
            Workflow.sleep(500); // Small delay to avoid busy waiting
        }
        return savedPersonId;
    }
}
