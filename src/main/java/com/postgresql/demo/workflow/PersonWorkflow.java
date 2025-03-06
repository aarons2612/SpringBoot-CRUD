package com.postgresql.demo.workflow;

import com.postgresql.demo.model.Demo;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface PersonWorkflow {

    @WorkflowMethod
    void processPerson(Demo person);
    
}
