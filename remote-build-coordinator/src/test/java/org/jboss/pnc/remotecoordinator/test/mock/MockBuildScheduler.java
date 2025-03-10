/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014-2022 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.pnc.remotecoordinator.test.mock;

import lombok.Setter;
import org.jboss.pnc.common.graph.GraphUtils;
import org.jboss.pnc.enums.BuildCoordinationStatus;
import org.jboss.pnc.enums.BuildStatus;
import org.jboss.pnc.mock.datastore.BuildTaskRepositoryMock;
import org.jboss.pnc.model.Base32LongID;
import org.jboss.pnc.model.User;
import org.jboss.pnc.remotecoordinator.builder.RexBuildScheduler;
import org.jboss.pnc.spi.BuildResult;
import org.jboss.pnc.spi.builddriver.BuildDriverResult;
import org.jboss.pnc.spi.coordinator.CompletionStatus;
import org.jboss.pnc.spi.coordinator.DefaultBuildTaskRef;
import org.jboss.pnc.spi.coordinator.RemoteBuildTask;
import org.jboss.pnc.spi.exception.RemoteRequestException;
import org.jboss.pnc.spi.exception.ScheduleException;
import org.jboss.pnc.spi.executor.BuildExecutionConfiguration;
import org.jboss.pnc.spi.repositorymanager.RepositoryManagerResult;
import org.jboss.util.graph.Graph;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.jboss.pnc.spi.coordinator.CompletionStatus.CANCELLED;
import static org.jboss.pnc.spi.coordinator.CompletionStatus.FAILED;
import static org.jboss.pnc.spi.coordinator.CompletionStatus.NO_REBUILD_REQUIRED;
import static org.jboss.pnc.spi.coordinator.CompletionStatus.SYSTEM_ERROR;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ApplicationScoped
@Alternative
public class MockBuildScheduler implements RexBuildScheduler {

    @Setter(onMethod_ = { @Inject })
    protected BuildTaskRepositoryMock taskRepositoryMock;

    @Setter
    private ScheduleException scheduleException;

    private List<Graph<RemoteBuildTask>> scheduleRequests = new ArrayList<>();

    public static BuildResult buildResult() {
        return buildResult(CompletionStatus.SUCCESS);
    }

    public static BuildResult buildResult(CompletionStatus status) {
        return new BuildResult(
                status,
                Optional.empty(),
                Optional.of(mock(BuildExecutionConfiguration.class)),
                Optional.of(buildDriverResult()),
                Optional.of(repoManagerResult()),
                Optional.empty(),
                Optional.empty());
    }

    private static BuildDriverResult buildDriverResult() {
        BuildDriverResult mock = mock(BuildDriverResult.class);
        when(mock.getBuildStatus()).thenReturn(BuildStatus.SUCCESS);
        return mock;
    }

    private static RepositoryManagerResult repoManagerResult() {
        RepositoryManagerResult mock = mock(RepositoryManagerResult.class);
        when(mock.getCompletionStatus()).thenReturn(CompletionStatus.SUCCESS);
        return mock;
    }

    @Override
    public void startBuilding(Graph<RemoteBuildTask> buildGraph, User user, Base32LongID buildConfigSetRecordId)
            throws ScheduleException {
        scheduleRequests.add(buildGraph);
        if (scheduleException != null) {
            throw scheduleException;
        }
        Collection<RemoteBuildTask> sourceVerticies = GraphUtils.unwrap(buildGraph.getVerticies());
        for (RemoteBuildTask buildTask : sourceVerticies) {
            taskRepositoryMock.addTask(
                    DefaultBuildTaskRef.builder()
                            .id(buildTask.getId())
                            .status(BuildCoordinationStatus.BUILDING)
                            .build());
        }
    }

    @Override
    public void cancel(String taskId) {
    }

    public void reset() {
        taskRepositoryMock.clear();
        scheduleRequests.clear();
        scheduleException = null;
    }

    @NotNull
    private static BuildResult mockBuildResult(BuildCoordinationStatus status) {
        BuildResult result;
        switch (status) {
            case REJECTED:
            case REJECTED_FAILED_DEPENDENCIES:
            case DONE_WITH_ERRORS:
                result = buildResult(FAILED);
                break;
            case REJECTED_ALREADY_BUILT:
                result = buildResult(NO_REBUILD_REQUIRED);
            case SYSTEM_ERROR:
                result = buildResult(SYSTEM_ERROR);
                break;
            case CANCELLED:
                result = buildResult(CANCELLED);
                break;
            case DONE:
            default:
                result = buildResult();
        }
        return result;
    }

    public List<Graph<RemoteBuildTask>> getScheduleRequests() {
        return scheduleRequests;
    }

    public long activeBuildTaskCount() {
        return taskRepositoryMock.getUnfinishedTasks().size();
    }

    @Override
    public long getBuildQueueSize() throws RemoteRequestException {
        return 10;
    }

    @Override
    public void setBuildQueueSize(long queueSize) {
    }
}
