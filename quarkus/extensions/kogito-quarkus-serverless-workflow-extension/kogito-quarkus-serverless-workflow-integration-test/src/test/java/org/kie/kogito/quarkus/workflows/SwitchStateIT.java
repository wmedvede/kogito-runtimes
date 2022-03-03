/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.quarkus.workflows;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonCloudEventData;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.path.json.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.quarkus.workflows.WorkflowTestUtils.newProcessInstance;
import static org.kie.kogito.quarkus.workflows.WorkflowTestUtils.newProcessInstanceAndGetId;

@QuarkusIntegrationTest
class SwitchStateIT {

    private static final String SWITCH_STATE_SERVICE_URL = "/switch_state";
    private static final String DECISION_PATH = "workflowdata.decision";
    private static final String DECISION_APPROVED = "Approved";
    private static final String DECISION_DENIED = "Denied";
    private static final String DECISION_INVALIDATED = "Invalidated";

    @ConfigProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    String kafkaBootstrapServers;

    ObjectMapper objectMapper;

    KafkaTestClient kafkaClient;

    //@Test
    void switchStateApprovedCondition() {
        // Start a new process instance that must be "Approved" and check the result.
        JsonPath result = newProcessInstance(SWITCH_STATE_SERVICE_URL, buildProcessInput(18));
        assertDecision(result, DECISION_APPROVED);
    }

    //@Test
    void switchStateDeniedCondition() {
        // Start a new process instance that must be "Denied" and check the result.
        JsonPath result = newProcessInstance(SWITCH_STATE_SERVICE_URL, buildProcessInput(10));
        assertDecision(result, DECISION_DENIED);
    }

    //@Test
    void switchStateDefaultCondition() {
        // Start a new process instance that must go through the default condition check the result.
        JsonPath result = newProcessInstance(SWITCH_STATE_SERVICE_URL, buildProcessInput(-20));
        assertDecision(result, DECISION_INVALIDATED);
    }

    @Test
    void switchStateKAKA() throws Exception {
        // Start a new process instance that must go through the default condition check the result.
        String processInstanceId = newProcessInstanceAndGetId("switch_state_event_condition_timeouts_transition", "{\"workflowdata\" : \"\" }");

        // prepare and send the response to the created process via kafka
        String response = objectMapper.writeValueAsString(CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create(""))
                .withType("visa_approved_in")
                .withTime(OffsetDateTime.now())
                .withExtension(
                        "kogitoprocrefid", processInstanceId)
                .withData(JsonCloudEventData.wrap(objectMapper.createObjectNode().put("answer", "blabla")))
                .build());
        kafkaClient.produce(response, "visa_approved_topic");

    }

    protected static void assertDecision(JsonPath jsonPath, String expectedDecision) {
        String currentDecision = jsonPath.get(DECISION_PATH);
        assertThat(currentDecision).isEqualTo(expectedDecision);
    }

    protected static String buildProcessInput(int age) {
        return "{\"workflowdata\": {\"age\": " + age + "} }";
    }

}
