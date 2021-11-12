/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.jobs.api.event.experimental;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

import org.kie.kogito.cloudevents.extension.KogitoProcessExtension;
import org.kie.kogito.jobs.api.Job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonFormat;

public class JobsEventBuilder {

    public static final String CREATE_PROCESS_INSTANCE_JOB_REQUEST = "CreateProcessInstanceJobRequest";

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(JsonFormat.getCloudEventJacksonModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    //    public static <E> Optional<CloudEvent> build(String id, URI source, E data, Class<E> dataType) {

    public static CloudEvent buildCreateProcessInstanceJobEvent(String id,
            URI source,
            Job job) {
        KogitoProcessExtension processExtension = new KogitoProcessExtension();
        processExtension.setKogitoProcessInstanceId("BLABLA");
        String jsonData;
        try {
            jsonData = OBJECT_MAPPER.writeValueAsString(job);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        CloudEventBuilder builder = CloudEventBuilder.v1()
                .withId(id)
                .withSource(source)
                .withType(CREATE_PROCESS_INSTANCE_JOB_REQUEST)
                .withTime(OffsetDateTime.now())
                .withData(jsonData.getBytes(StandardCharsets.UTF_8))
                .withExtension(processExtension);
        return builder.build();
    }

    public static CloudEvent deserialize(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, CloudEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
