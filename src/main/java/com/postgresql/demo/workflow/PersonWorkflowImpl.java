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
                    .setStartToCloseTimeout(Duration.ofSeconds(10)) // ⬅️ Required timeout
                    .build()
    );

    @Override
    public void processPerson(Demo person) {
        Demo savedPerson = activities.addPerson(person);
        System.out.println("Workflow started for person: " + savedPerson.getId());

        // Call activities
        activities.validatePerson(savedPerson.getId());
        activities.processPersonData(savedPerson.getId());
        activities.sendNotification(savedPerson.getId());

        System.out.println("Workflow completed for person: " + savedPerson.getId());
    }
}
