package com.postgresql.demo.services;

import com.postgresql.demo.model.Demo;
import com.postgresql.demo.workflow.PersonWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.stereotype.Service;

@Service
public class TemporalService {

    private final WorkflowClient client;

    public TemporalService() {
        this.client = WorkflowClient.newInstance(WorkflowServiceStubs.newInstance());
    }

    public void startPersonWorkflow(Demo person) {
        PersonWorkflow workflow = client.newWorkflowStub(
                PersonWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue("PERSON_TASK_QUEUE")
                        .build()
        );

        WorkflowClient.start(workflow::processPerson, person);
    }
}
