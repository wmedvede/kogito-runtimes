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

public class CancelJobRequestEvent extends ProcessInstanceContextJobCloudEvent<CancelJobRequestEvent.JobId> {

    public static final String CANCEL_JOB_REQUEST = "CancelJobRequest";

    public CancelJobRequestEvent(URI source,
            String id,
            String processInstanceId,
            String processId,
            String rootProcessInstanceId,
            String rootProcessId,
            String kogitoAddons) {
        super(CANCEL_JOB_REQUEST, source, new JobId(id), processInstanceId, processId, rootProcessInstanceId, rootProcessId, kogitoAddons);
    }

    public static class JobId {

        private String id;

        public JobId() {
            // marshalling constructor.
        }

        public JobId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    @Override
    public String toString() {
        return "CancelJobRequestEvent{} " + super.toString();
    }
}