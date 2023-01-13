/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.jobs.management.springboot;

import java.net.URI;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.ProcessJobDescription;
import org.kie.kogito.jobs.TimerJobId;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kie.kogito.jobs.service.api.event.serialization.SerializationUtils.registerDescriptors;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpringRestJobsServiceTest {

    private static final String CALLBACK_URL = "http://localhost:8080";
    private static final String JOB_SERVICE_URL = "http://localhost:8085";
    private static final String JOB_ID = "456";
    private static final long TIMER_ID = 123;
    private static final String PROCESS_ID = "PROCESS_ID";
    private static final String PROCESS_INSTANCE_ID = "PROCESS_INSTANCE_ID";
    private static final String ROOT_PROCESS_ID = "ROOT_PROCESS_ID";
    private static final String ROOT_PROCESS_INSTANCE_ID = "ROOT_PROCESS_INSTANCE_ID";
    private static final String NODE_INSTANCE_ID = "NODE_INSTANCE_ID";
    private static final ZonedDateTime EXPIRATION_TIME = ZonedDateTime.parse("2023-01-13T10:20:30.000001+01:00[Europe/Madrid]");

    private SpringRestJobsService tested;

    @Mock
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.objectMapper = new Jackson2ObjectMapperBuilder().build();
        registerDescriptors(objectMapper);
        this.tested = new SpringRestJobsService(JOB_SERVICE_URL, CALLBACK_URL, restTemplate, objectMapper);
        tested.initialize();
    }

    @Test
    void testScheduleProcessJob() {
        ProcessJobDescription processJobDescription = ProcessJobDescription.of(ExactExpirationTime.of(EXPIRATION_TIME),
                1,
                PROCESS_ID);
        assertThatThrownBy(() -> tested.scheduleProcessJob(processJobDescription))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void testScheduleProcessInstanceJob() throws Exception {
        when(restTemplate.postForEntity(any(URI.class), any(HttpEntity.class), eq(String.class))).thenReturn(ResponseEntity.ok().build());
        ProcessInstanceJobDescription processInstanceJobDescription = ProcessInstanceJobDescription.of(new TimerJobId(TIMER_ID),
                ExactExpirationTime.of(EXPIRATION_TIME),
                PROCESS_INSTANCE_ID,
                ROOT_PROCESS_INSTANCE_ID,
                PROCESS_ID,
                ROOT_PROCESS_ID,
                NODE_INSTANCE_ID);
        tested.scheduleProcessInstanceJob(processInstanceJobDescription);
        ArgumentCaptor<HttpEntity<String>> jobArgumentCaptor = forClass(HttpEntity.class);
        verify(restTemplate).postForEntity(eq(tested.getJobsServiceUri()),
                jobArgumentCaptor.capture(),
                eq(String.class));
        HttpEntity<String> request = jobArgumentCaptor.getValue();
        String json = request.getBody();
        Job job = objectMapper.readValue(json, Job.class);
        assertThat(job.getId()).isEqualTo(processInstanceJobDescription.id());
        assertThat(job.getId()).contains(Long.toString(TIMER_ID));
        assertThat(job.getRecipient())
                .isNotNull()
                .isInstanceOf(HttpRecipient.class);
        HttpRecipient<?> httpRecipient = (HttpRecipient<?>) job.getRecipient();
        assertThat(httpRecipient.getMethod()).isEqualTo("POST");
        assertThat(httpRecipient.getUrl()).isEqualTo("%s/management/jobs/%s/instances/%s/timers/%s",
                CALLBACK_URL,
                PROCESS_ID,
                PROCESS_INSTANCE_ID,
                processInstanceJobDescription.id());
        assertThat(httpRecipient.getHeaders())
                .hasSize(5)
                .containsEntry("processId", PROCESS_ID)
                .containsEntry("processInstanceId", PROCESS_INSTANCE_ID)
                .containsEntry("rootProcessId", ROOT_PROCESS_ID)
                .containsEntry("rootProcessInstanceId", ROOT_PROCESS_INSTANCE_ID)
                .containsEntry("nodeInstanceId", NODE_INSTANCE_ID);
        assertThat(httpRecipient.getPayload()).isNull();
        assertThat(job.getSchedule())
                .isNotNull()
                .isInstanceOf(TimerSchedule.class);
        TimerSchedule timerSchedule = (TimerSchedule) job.getSchedule();
        assertThat(timerSchedule.getStartTime()).isEqualTo(EXPIRATION_TIME.toOffsetDateTime());
    }

    @Test
    void testCancelJob() {
        tested.cancelJob(JOB_ID);
        verify(restTemplate).delete(tested.getJobsServiceUri() + "/{id}", JOB_ID);
    }
}
