package com.postgresql.demo.activities;

import com.postgresql.demo.model.Demo;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface PersonActivities {

    @ActivityMethod
    Demo addPerson(Demo person);

    @ActivityMethod
    void validatePerson(Long id);

    @ActivityMethod
    void processPersonData(Long id);

    @ActivityMethod
    void sendNotification(Long id);
}
