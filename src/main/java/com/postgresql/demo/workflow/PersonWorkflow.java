package com.postgresql.demo.workflow;

import com.postgresql.demo.model.Demo;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.UpdateMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface PersonWorkflow {

    @WorkflowMethod
    Demo processPerson(Demo person);

    // @QueryMethod
    // Long getPersonId();

    // @UpdateMethod
    // Long updatePersonId(); 
}
