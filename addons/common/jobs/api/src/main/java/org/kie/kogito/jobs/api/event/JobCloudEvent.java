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
package org.kie.kogito.jobs.api.event;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class JobCloudEvent<T> {

    static final String SPEC_VERSION = "1.0";

    @JsonProperty("specversion")
    private String specVersion;
    private String id;
    private URI source;
    private String type;
    private ZonedDateTime time;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String subject;
    @JsonProperty("datacontenttype")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String dataContentType;
    @JsonProperty("dataschema")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String dataSchema;

    private T data;

    protected JobCloudEvent() {
        //TODO, remove if not required for marshalling purposes.
    }

    protected JobCloudEvent(String type,
            URI source,
            T data) {
        this.specVersion = SPEC_VERSION;
        this.id = UUID.randomUUID().toString();
        this.source = source;
        this.type = type;
        this.time = ZonedDateTime.now();
        this.data = data;
    }

    protected JobCloudEvent(String type,
            URI source,
            T data,
            String subject,
            String dataContentType,
            String dataSchema) {
        this(type, source, data);
        this.subject = subject;
        this.dataContentType = dataContentType;
        this.dataSchema = dataSchema;
    }

    public URI getSource() {
        return source;
    }

    public String getSpecVersion() {
        return specVersion;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public T getData() {
        return data;
    }

    public String getDataContentType() {
        return dataContentType;
    }

    public String getDataSchema() {
        return dataSchema;
    }

    public String getSubject() {
        return subject;
    }

    @Override
    public String toString() {
        return "JobCloudEvent{" +
                "specVersion='" + specVersion + '\'' +
                ", id='" + id + '\'' +
                ", source=" + source +
                ", type='" + type + '\'' +
                ", time=" + time +
                ", subject='" + subject + '\'' +
                ", dataContentType='" + dataContentType + '\'' +
                ", dataSchema='" + dataSchema + '\'' +
                ", data=" + data +
                '}';
    }
}
