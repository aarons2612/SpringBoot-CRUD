package com.postgresql.demo.worker;

import com.postgresql.demo.activities.PersonActivitiesImpl;
import com.postgresql.demo.repo.DemoRepo;
import com.postgresql.demo.workflow.PersonWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.stereotype.Component;

@Component
public class PersonWorker {

    public PersonWorker(DemoRepo demoRepo) { // ✅ Inject repository here
        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
        WorkflowClient client = WorkflowClient.newInstance(service);
        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker("PERSON_TASK_QUEUE");

        // ✅ Pass repo to PersonActivitiesImpl
        PersonActivitiesImpl personActivities = new PersonActivitiesImpl(demoRepo);

        worker.registerWorkflowImplementationTypes(PersonWorkflowImpl.class);
        worker.registerActivitiesImplementations(personActivities);

        factory.start();
        System.out.println("🚀 Temporal Worker Started: Listening on PERSON_TASK_QUEUE");
    }
}
