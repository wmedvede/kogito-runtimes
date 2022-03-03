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

import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.TimerNode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.api.definition.process.Node;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.serverless.workflow.WorkflowTestUtils.assertClassAndGetNode;
import static org.kie.kogito.serverless.workflow.WorkflowTestUtils.assertExclusiveSplit;
import static org.kie.kogito.serverless.workflow.WorkflowTestUtils.assertHasName;
import static org.kie.kogito.serverless.workflow.WorkflowTestUtils.assertHasNodesSize;
import static org.kie.kogito.serverless.workflow.WorkflowTestUtils.assertIsConnected;
import static org.kie.kogito.serverless.workflow.WorkflowTestUtils.assertProcessMainParams;

class SwitchStateServerlessWorkflowParsingTest extends AbstractServerlessWorkflowParsingTest {

    //    @ParameterizedTest
    //    @ValueSource(strings = { "/exec/switch-state-data-condition-transition.sw.json", "/exec/switch-state-data-condition-transition.sw.yml" })
    void switchStateDataConditionTransition(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertProcessMainParams(process,
                "switch_state_data_condition_transition",
                "Switch State Data Condition Transition Test",
                "1.0",
                "org.kie.kogito.serverless",
                RuleFlowProcess.PUBLIC_VISIBILITY);

        assertHasNodesSize(process, 8);
        StartNode processStartNode = assertClassAndGetNode(process, 0, StartNode.class);
        EndNode processEndNode1 = assertClassAndGetNode(process, 1, EndNode.class);
        EndNode processEndNode2 = assertClassAndGetNode(process, 2, EndNode.class);
        EndNode processEndNode3 = assertClassAndGetNode(process, 3, EndNode.class);
        Split splitNode = assertClassAndGetNode(process, 4, Split.class);
        assertExclusiveSplit(splitNode, "ChooseOnAge", 2);
        ActionNode approveTransitionActionNode = assertClassAndGetNode(process, 5, ActionNode.class);
        assertHasName(approveTransitionActionNode, "Approve");
        ActionNode denyTransitionActionNode = assertClassAndGetNode(process, 6, ActionNode.class);
        assertHasName(denyTransitionActionNode, "Deny");
        ActionNode defaultConditionTransitionActionNode = assertClassAndGetNode(process, 7, ActionNode.class);
        assertHasName(defaultConditionTransitionActionNode, "Invalidate");

        assertIsConnected(processStartNode, splitNode);
        assertIsConnected(splitNode, approveTransitionActionNode);
        assertIsConnected(approveTransitionActionNode, processEndNode1);
        assertIsConnected(splitNode, denyTransitionActionNode);
        assertIsConnected(denyTransitionActionNode, processEndNode2);
        assertIsConnected(splitNode, defaultConditionTransitionActionNode);
        assertIsConnected(defaultConditionTransitionActionNode, processEndNode3);
    }
    //    @ParameterizedTest
    //    @ValueSource(strings = { "/exec/switch-state-event-condition-timeouts.sw.json" /*, "/exec/switch-state-data-condition-transition.sw.yml"*/ })
    //    void switchStateEventDataConditionTimeouts(String workflowLocation) throws Exception {
    //        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
    //        assertProcessMainParams(process,
    //                                "switch_state_event_condition_timeouts",
    //                                "Switch State Event Based Timeouts Test",
    //                                "1.0",
    //                                "org.kie.kogito.serverless",
    //                                RuleFlowProcess.PUBLIC_VISIBILITY);
    //
    //        int i = 0;
    //
    //    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/switch-state-event-condition-timeouts_transition2.sw.json" /* , "/exec/switch-state-data-condition-transition.sw.yml" */ })
    void switchStateEventDataConditionTimeouts2(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertProcessMainParams(process,
                "switch_state_event_condition_timeouts_transition2",
                "Switch State Event Condition Timeouts Transition2 Test",
                "1.0",
                "org.kie.kogito.serverless",
                RuleFlowProcess.PUBLIC_VISIBILITY);

        int i = 0;

    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/switch-state-event-condition-timeouts-end.sw.json" /* , "/exec/switch-state-data-condition-transition.sw.yml" */ })
    void switchStateEventDataConditionTimeoutsEnd(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertProcessMainParams(process,
                "switch_state_event_condition_timeouts_end",
                "Switch State Event Condition Timeouts End Test",
                "1.0",
                "org.kie.kogito.serverless",
                RuleFlowProcess.PUBLIC_VISIBILITY);

        assertHasNodesSize(process, 12);

        StartNode processStartNode = assertClassAndGetNode(process, 0, StartNode.class);
        EndNode endNode1 = assertClassAndGetNode(process, 1, EndNode.class);
        EndNode endNode2 = assertClassAndGetNode(process, 2, EndNode.class);
        Split splitNode = assertClassAndGetNode(process, 3, Split.class);
        assertHasName(splitNode, "ChooseOnEvent");
        ActionNode approvedVisaState = assertClassAndGetNode(process, 4, ActionNode.class);
        assertHasName(approvedVisaState, "ApprovedVisa");
        ActionNode deniedVisaState = assertClassAndGetNode(process, 5, ActionNode.class);
        assertHasName(deniedVisaState, "DeniedVisa");
        TimerNode timeoutTimerNode = assertClassAndGetNode(process, 6, TimerNode.class);
        assertThat(timeoutTimerNode.getTimer().getDelay()).isEqualTo("PT2M");
        EndNode endNode3 = assertClassAndGetNode(process, 7, EndNode.class);
        EventNode visaApprovedEventNode = assertClassAndGetNode(process, 8, EventNode.class);
        assertHasName(visaApprovedEventNode, "visaApprovedEvent");
        ActionNode visaApprovedEventNodeMergeAction = assertClassAndGetNode(process, 9, ActionNode.class);
        EventNode visaDeniedEventNode = assertClassAndGetNode(process, 10, EventNode.class);
        assertHasName(visaDeniedEventNode, "visaDeniedEvent");
        ActionNode visaDeniedEventNodeMergeAction = assertClassAndGetNode(process, 11, ActionNode.class);

        assertIsConnected(processStartNode, splitNode);
        assertIsConnected(splitNode, timeoutTimerNode);
        assertIsConnected(timeoutTimerNode, endNode3);

        assertIsConnected(splitNode, visaApprovedEventNode);
        assertIsConnected(visaApprovedEventNode, visaApprovedEventNodeMergeAction);
        assertIsConnected(visaApprovedEventNodeMergeAction, approvedVisaState);
        assertIsConnected(approvedVisaState, endNode1);

        assertIsConnected(splitNode, visaDeniedEventNode);
        assertIsConnected(visaDeniedEventNode, visaDeniedEventNodeMergeAction);
        assertIsConnected(visaDeniedEventNodeMergeAction, deniedVisaState);
        assertIsConnected(deniedVisaState, endNode2);

    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/eventbased-switch-state.sw.json", "/exec/eventbased-switch-state.sw.yml" })
    void testEventBasedSwitchWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertEquals("eventswitchworkflow", process.getId());
        assertEquals("event-switch-wf", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(12, process.getNodes().length);

        Node node = process.getNodes()[0];
        assertTrue(node instanceof StartNode);
        node = process.getNodes()[1];
        assertTrue(node instanceof EndNode);
        node = process.getNodes()[2];
        assertTrue(node instanceof EndNode);
        node = process.getNodes()[3];
        assertTrue(node instanceof ActionNode);
        node = process.getNodes()[4];
        assertTrue(node instanceof Split);
        node = process.getNodes()[5];
        assertTrue(node instanceof ActionNode);
        node = process.getNodes()[6];
        assertTrue(node instanceof ActionNode);
        node = process.getNodes()[7];
        assertTrue(node instanceof ActionNode);
        node = process.getNodes()[8];
        assertTrue(node instanceof EventNode);
        node = process.getNodes()[10];
        assertTrue(node instanceof EventNode);

        Split split = (Split) process.getNodes()[4];
        assertEquals("ChooseOnEvent", split.getName());
        assertEquals(Split.TYPE_XAND, split.getType());

        EventNode firstEventNode = (EventNode) process.getNodes()[8];
        assertEquals("visaApprovedEvent", firstEventNode.getName());

        EventNode secondEventNode = (EventNode) process.getNodes()[10];
        assertEquals("visaDeniedEvent", secondEventNode.getName());
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/switch-state-produce-events.sw.json", "/exec/switch-state-produce-events.sw.yml" })
    void testSwitchProduceEventsOnTransitionWorkflow(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertEquals("switchworkflow", process.getId());
        assertEquals("switch-wf", process.getName());
        assertEquals("1.0", process.getVersion());
        assertEquals("org.kie.kogito.serverless", process.getPackageName());
        assertEquals(RuleFlowProcess.PUBLIC_VISIBILITY, process.getVisibility());

        assertEquals(15, process.getNodes().length);

        Split split = (Split) process.getNodes()[4];
        assertEquals("ChooseOnAge", split.getName());
        assertEquals(2, split.getType());
        assertEquals(2, split.getConstraints().size());

        boolean haveDefaultConstraint = false;
        for (Constraint constraint : split.getConstraints().values()) {
            haveDefaultConstraint = haveDefaultConstraint || constraint.isDefault();
        }

        assertTrue(haveDefaultConstraint);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/switch-state-end-condition.sw.json", "/exec/switch-state-end-condition.sw.yml" })
    void switchStateEndConditions(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertProcessMainParams(process,
                "switch_state_end_condition",
                "Switch State End Condition Test",
                "1.0",
                "org.kie.kogito.serverless",
                RuleFlowProcess.PUBLIC_VISIBILITY);

        assertHasNodesSize(process, 6);

        StartNode processStartNode = assertClassAndGetNode(process, 0, StartNode.class);
        ActionNode actionNode1 = assertClassAndGetNode(process, 1, ActionNode.class);
        assertHasName(actionNode1, "AddInfo");
        Split splitNode = assertClassAndGetNode(process, 2, Split.class);
        assertExclusiveSplit(splitNode, "ChooseOnAge", 2);
        EndNode processEndNode1 = assertClassAndGetNode(process, 3, EndNode.class);
        EndNode processEndNode2 = assertClassAndGetNode(process, 4, EndNode.class);
        EndNode processEndNode3 = assertClassAndGetNode(process, 5, EndNode.class);

        assertIsConnected(processStartNode, actionNode1);
        assertIsConnected(actionNode1, splitNode);
        assertIsConnected(splitNode, processEndNode1);
        assertIsConnected(splitNode, processEndNode2);
        assertIsConnected(splitNode, processEndNode3);
    }
}
