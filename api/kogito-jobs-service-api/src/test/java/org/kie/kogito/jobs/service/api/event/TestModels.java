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

package org.kie.kogito.jobs.service.api.event;

import java.net.URI;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.api.*;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientBinaryPayloadData;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientStringPayloadData;
import org.kie.kogito.jobs.service.api.recipient.kafka.KafkaRecipient;
import org.kie.kogito.jobs.service.api.recipient.kafka.KafkaRecipientStringPayloadData;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipient;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipientPayloadData;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;

public class TestModels {

    private EventFormat eventFormat;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        eventFormat = EventFormatProvider.getInstance().resolveFormat("application/cloudevents+json");
        objectMapper = new ObjectMapper();
        objectMapper.registerModules(JsonFormat.getCloudEventJacksonModule());
        registerDescriptors(objectMapper);
    }

    @Test
    void createHttpRecipientWithStringPayloadData() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("name", "Tioagop");
        jsonNode.put("age", 15);
        String serialized = objectMapper.writeValueAsString(jsonNode);

        HttpRecipient<HttpRecipientStringPayloadData> recipient = HttpRecipient.builder()
                .forStringPayload()
                .url("http://examples.com")
                .method("POST")
                .payload(HttpRecipientStringPayloadData.from(serialized))
                .url("http://examples.com")
                .queryParam("param1", "param1value")
                .header("header1", "header1Value")
                .header("header2", "header1Value")
                .build();

        Job job = Job.builder()
                .recipient(recipient)
                .build();

        registerDescriptors(objectMapper);

        String json = objectMapper.writeValueAsString(recipient);
        String jobJson = objectMapper.writeValueAsString(job);

        System.out.println("Recipient json" + json);
        System.out.println("Job json" + jobJson);

        HttpRecipient<?> result = objectMapper.readValue(json, HttpRecipient.class);

        HttpRecipient<HttpRecipientStringPayloadData> result2 = objectMapper.readValue(json, new TypeReference<>() {
        });

        System.out.println(json);
    }

    @Test
    void createHttpRecipientWithBinaryPayloadData() throws Exception {

        HttpRecipient<HttpRecipientBinaryPayloadData> recipient = HttpRecipient.builder()
                .forBinaryPayload()
                .url("http://examples.com")
                .method("POST")
                .payload(HttpRecipientBinaryPayloadData.from("你好你好嗎用中文".getBytes()))
                .url("http://examples.com")
                .queryParam("param1", "param1value")
                .header("header1", "header1Value")
                .header("header2", "header1Value")
                .build();

        Job job = Job.builder()
                .recipient(recipient)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        registerDescriptors(objectMapper);

        String json = objectMapper.writeValueAsString(recipient);
        String jobJson = objectMapper.writeValueAsString(job);

        System.out.println("Recipient json" + json);
        System.out.println("Job json" + jobJson);

        HttpRecipient<?> result = objectMapper.readValue(json, HttpRecipient.class);

        HttpRecipient<HttpRecipientBinaryPayloadData> result2 = objectMapper.readValue(json, new TypeReference<>() {
        });

        System.out.println(json);
    }

    @Test
    void createSinkRecipient() throws Exception {

        /*
         * 
         * When we work with json format
         * 
         * 1) if datacontenttype == null -> It's assumed as datacontenttype="application/json".
         * 2) if datacontenttype="application/json". what we have in the data is assumed to be a valid json (
         * 
         * 
         * 
         * 
         * 
         */

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("name", "Michael");
        objectNode.put("surname", "Jackson");

        CloudEvent event = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                //.withDataContentType("application/xml")
                .withDataContentType("application/octet")
                .withType("test.event")
                .withSource(URI.create("http:/example.com"))
                .withData(BytesCloudEventData.wrap("true".getBytes()))
                //.withData(JsonCloudEventData.wrap(objectNode))
                //.withData("hello workd".getBytes())
                //.withData(StringCloudEventData.wrap(objectNode))
                .build();

        SinkRecipient recipient = SinkRecipient.builder()
                .sinkUrl("http://sink-url.com")
                .contentMode(SinkRecipient.ContentMode.BINARY)
                .payload(SinkRecipientPayloadData.from(event))
                .build();

        Job job = Job.builder()
                .recipient(recipient)
                .build();

        String eventJson = new String(eventFormat.serialize(event));
        String json = objectMapper.writeValueAsString(recipient);
        String jobJson = objectMapper.writeValueAsString(job);

        System.out.println("Event json" + eventJson);
        System.out.println("Recipient json" + json);
        System.out.println("Job json" + jobJson);

        SinkRecipient result = objectMapper.readValue(json, SinkRecipient.class);

        System.out.println(json);
    }

    @Test
    void kafkaRecipient() {
        KafkaRecipient<KafkaRecipientStringPayloadData> recipient = KafkaRecipient.builder()
                .forStringPayload()
                .payload(KafkaRecipientStringPayloadData.from("message"))
                .build();
    }

    public static void registerDescriptors(ObjectMapper objectMapper) {
        for (RecipientDescriptor<?> descriptor : RecipientDescriptorRegistry.getInstance().getDescriptors()) {
            objectMapper.registerSubtypes(new NamedType(descriptor.getType(), descriptor.getName()));
        }
        for (ScheduleDescriptor<?> descriptor : ScheduleDescriptorRegistry.getInstance().getDescriptors()) {
            objectMapper.registerSubtypes(new NamedType(descriptor.getType(), descriptor.getName()));
        }
    }
}
