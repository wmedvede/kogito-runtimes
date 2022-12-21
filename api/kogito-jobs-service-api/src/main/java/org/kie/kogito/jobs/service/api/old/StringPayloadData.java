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

package org.kie.kogito.jobs.service.api.old;

import org.kie.kogito.jobs.service.api.PayloadData;

import com.fasterxml.jackson.annotation.JsonProperty;

//@Schema(allOf = { PayloadData.class })
public class StringPayloadData extends PayloadData<String> {

    @JsonProperty("data")
    private String dataString;

    public StringPayloadData() {
        // Marshalling constructor.
    }

    private StringPayloadData(String data) {
        this.dataString = data;
    }

    public String getData() {
        return dataString;
    }

    public static StringPayloadData from(String data) {
        return new StringPayloadData(data);
    }
}
