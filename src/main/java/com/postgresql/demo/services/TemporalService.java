package com.postgresql.demo.services;

import com.postgresql.demo.model.Demo;
import com.postgresql.demo.workflow.PersonWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TemporalService {

    private final WorkflowClient client;

    public TemporalService() {
        this.client = WorkflowClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
    }

    public String startPersonWorkflow(Demo person) {
        String workflowId = "PERSON_WORKFLOW_" + System.currentTimeMillis();

        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue("PERSON_TASK_QUEUE")
                .setWorkflowRunTimeout(Duration.ofMinutes(5)) // Ensures workflow doesn't time out early
                .setWorkflowId(workflowId) // Unique ID
                .build();

        // Create a workflow stub
        PersonWorkflow workflow = client.newWorkflowStub(PersonWorkflow.class, options);

        // Start workflow asynchronously
        WorkflowClient.start(workflow::processPerson, person);

        return workflowId; // ✅ Return workflow ID so we can query later
    }

    public Long getPersonId(String workflowId) {
        PersonWorkflow workflow = client.newWorkflowStub(PersonWorkflow.class, workflowId);

        int maxRetries = 10; // Max wait time: 10 seconds
        int retryCount = 0;
        Long personId = null;

        while (retryCount < maxRetries) {
            try {
                Thread.sleep(1000); // Wait 1 second before checking
                personId = workflow.getPersonId(); // ✅ Query the workflow for the stored personId
                if (personId != null) break; // Exit loop when we get the ID
            } catch (Exception e) {
                retryCount++;
            }
        }

        return personId;
    }
}
