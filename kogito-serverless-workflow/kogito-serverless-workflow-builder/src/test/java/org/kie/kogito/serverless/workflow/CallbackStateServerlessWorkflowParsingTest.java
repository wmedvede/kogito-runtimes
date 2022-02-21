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

package org.kie.kogito.serverless.workflow;

import java.util.List;

import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.node.BoundaryEventNode;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.StartNode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.api.definition.process.Connection;
import org.kie.api.definition.process.Node;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class CallbackStateServerlessWorkflowParsingTest extends AbstractServerlessWorkflowParsingTest {

    @ParameterizedTest
    @ValueSource(strings = { "/exec/callback-state.sw.json" })
    void testProduceCallbackState(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertThat(process.getId()).isEqualTo("callback-state");
        assertThat(process.getVersion()).isEqualTo("1.0");
        assertThat(process.getPackageName()).isEqualTo("org.kie.kogito.serverless");
        assertThat(process.getVisibility()).isEqualTo(RuleFlowProcess.PUBLIC_VISIBILITY);

        assertThat(process.getNodes()).hasSize(7);

        StartNode startNode = assertClassAndGetNode(process, 0, StartNode.class);
        EndNode endNode1 = assertClassAndGetNode(process, 1, EndNode.class);
        EndNode endNode2 = assertClassAndGetNode(process, 2, EndNode.class);
        CompositeContextNode callbackState = assertClassAndGetNode(process, 3, CompositeContextNode.class);
        assertThat(callbackState.getName()).isEqualTo("CallbackState");
        CompositeContextNode finalizeSuccessfulState = assertClassAndGetNode(process, 4, CompositeContextNode.class);
        assertThat(finalizeSuccessfulState.getName()).isEqualTo("FinalizeSuccessful");
        CompositeContextNode finalizeWithErrorState = assertClassAndGetNode(process, 5, CompositeContextNode.class);
        assertThat(finalizeWithErrorState.getName()).isEqualTo("FinalizeWithError");
        BoundaryEventNode callbackStateErrorBoundaryEvent = assertClassAndGetNode(process, 6, BoundaryEventNode.class);
        assertThat(callbackStateErrorBoundaryEvent.getName()).isEqualTo("Error-CallbackState-java.lang.Exception");

        assertIsConnectedWith(startNode, callbackState);

    }

    @SuppressWarnings("unchecked")
    <T extends Node> T assertClassAndGetNode(RuleFlowProcess process, int nodeIndex, Class<T> expectedNodeClass) {
        Node node = process.getNodes()[nodeIndex];
        assertThat(process.getNodes())
                .withFailMessage("Required nodeIndex: {} is out of range, the process.nodes has size: {}", nodeIndex, process.getNodes().length)
                .hasSizeGreaterThan(nodeIndex);
        assertThat(node).isInstanceOf(expectedNodeClass);
        return (T) node;
    }

    void assertIsConnectedWith(Node startNode, Node endNode) {
        assertThat(startNode.getOutgoingConnections())
                .withFailMessage("Node ({}, {}),  has no outgoing connections.",
                        startNode.getOutgoingConnections()
                                .values())
                .hasSizeGreaterThan(0);
        for (List<Connection> connections : startNode.getOutgoingConnections().values()) {
            for (Connection connection : connections) {
                if (connection.getTo() == endNode) {
                    return;
                }
            }
        }
        fail("Node ({}, {}), is not connected with Node ({}, {}).",
                startNode.getId(), startNode.getName(), endNode.getId(), endNode.getName());
    }
}
