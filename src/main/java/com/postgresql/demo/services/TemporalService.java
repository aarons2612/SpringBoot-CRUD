package com.postgresql.demo.services;

import com.postgresql.demo.model.Demo;
import com.postgresql.demo.workflow.PersonWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
// import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TemporalService {

    private final WorkflowClient client;

    public TemporalService() {
        this.client = WorkflowClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
    }

    public Demo startPersonWorkflow(Demo person) {
        
        String workflowId = "PERSON_WORKFLOW_" + System.currentTimeMillis();
    
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue("PERSON_TASK_QUEUE")
                .setWorkflowRunTimeout(Duration.ofMinutes(5))
                .setWorkflowId(workflowId)
                .build();
    
        // Create a workflow stub
        PersonWorkflow workflow = client.newWorkflowStub(PersonWorkflow.class, options);
    
        // Start the workflow and wait for it to complete (Synchronous execution)
        return workflow.processPerson(person);  // ✅ Returns the saved person object
    }
    
    // public Long getPersonId(String workflowId) {
    //     // Get a workflow stub using the workflowId
    //     PersonWorkflow workflow = client.newWorkflowStub(PersonWorkflow.class, workflowId);

    //     // Call the update method to fetch the person ID
    //     return workflow.updatePersonId();
    // }

}
