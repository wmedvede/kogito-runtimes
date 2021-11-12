/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.jobs.api.events;

import java.net.URI;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.kie.kogito.cloudevents.extension.KogitoProcessExtension;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.api.event.CreateProcessInstanceJobRequestEvent;
import org.kie.kogito.jobs.api.event.experimental.JobsEventBuilder;

import io.cloudevents.CloudEvent;

public class JobsEventBuilderTest {

    @Test
    void test() throws Exception {

        //CreateProcessInstanceJobRequestEvent requestEvent = new CreateProcessInstanceJobRequestEvent()
        URI uri = URI.create("http://localhost:8080/TestProcess");
        Job job = new Job("jobId",
                ZonedDateTime.now(),
                5,
                "callbackendpoint_XX",
                "processInstance_XX",
                "rootProcessInstanceId_XX",
                "processId_XX",
                "rootProcessId_XX",
                6L,
                7,
                "nodeInstanceId_XX");

        CloudEvent cloudEvent = JobsEventBuilder.buildCreateProcessInstanceJobEvent("1234", uri, job);
        String json = JobsEventBuilder.OBJECT_MAPPER.writeValueAsString(cloudEvent);
        System.out.println(json);

        CloudEvent cloudEventResult = JobsEventBuilder.deserialize(json);
        KogitoProcessExtension extension = new KogitoProcessExtension();
        extension.readFrom(cloudEventResult);
    }

    @Test
    void create() throws Exception {
        CreateProcessInstanceJobRequestEvent requestEvent = new CreateProcessInstanceJobRequestEvent(URI.create("http://localhost:8080"),
                new Job("123",
                        ZonedDateTime.now(),
                        1,
                        "callack",
                        "processInstanceId",
                        "rootProcessInstanceId",
                        "processId",
                        "rootProcessId",
                        5L,
                        4,
                        "nodeInstanceId"),
                "processInstanceId",
                "processId",
                "rootProcessInstanceId",
                "rootProcessId",
                "kaka");

        requestEvent.getData();

        requestEvent = new CreateProcessInstanceJobRequestEvent();

    }

}
