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
package org.jboss.pnc.rest.endpoints;

import org.jboss.pnc.dto.Build;
import org.jboss.pnc.dto.BuildConfiguration;
import org.jboss.pnc.dto.BuildConfigurationRef;
import org.jboss.pnc.dto.GroupBuild;
import org.jboss.pnc.dto.GroupConfiguration;
import org.jboss.pnc.dto.GroupConfigurationRef;
import org.jboss.pnc.dto.requests.GroupBuildRequest;
import org.jboss.pnc.dto.response.Page;
import org.jboss.pnc.facade.BuildTriggerer;
import org.jboss.pnc.facade.providers.api.BuildConfigurationProvider;
import org.jboss.pnc.facade.providers.api.BuildProvider;
import org.jboss.pnc.facade.providers.api.GroupBuildPageInfo;
import org.jboss.pnc.facade.providers.api.GroupBuildProvider;
import org.jboss.pnc.facade.providers.api.GroupConfigurationProvider;
import org.jboss.pnc.facade.validation.AlreadyRunningException;
import org.jboss.pnc.facade.validation.InvalidRequestException;
import org.jboss.pnc.rest.api.endpoints.GroupConfigurationEndpoint;
import org.jboss.pnc.rest.api.parameters.BuildsFilterParameters;
import org.jboss.pnc.rest.api.parameters.GroupBuildParameters;
import org.jboss.pnc.rest.api.parameters.GroupBuildsFilterParameters;
import org.jboss.pnc.rest.api.parameters.PageParameters;
import org.jboss.pnc.spi.BuildOptions;
import org.jboss.pnc.spi.exception.BuildConflictException;
import org.jboss.pnc.spi.exception.BuildRequestException;
import org.jboss.pnc.spi.exception.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.util.Optional;

import static org.jboss.pnc.rest.endpoints.BuildConfigurationEndpointImpl.checkBuildOptionsValidity;
import static org.jboss.pnc.rest.endpoints.BuildEndpointImpl.toBuildPageInfo;

/**
 *
 * @author Honza Brázdil &lt;jbrazdil@redhat.com&gt;
 */
@ApplicationScoped
public class GroupConfigurationEndpointImpl implements GroupConfigurationEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Inject
    private GroupConfigurationProvider provider;

    @Inject
    private BuildConfigurationProvider buildConfigurationProvider;

    @Inject
    private BuildProvider buildProvider;

    @Inject
    private GroupBuildProvider groupBuildProvider;

    @Inject
    private BuildTriggerer buildTriggerer;

    private EndpointHelper<Integer, GroupConfiguration, GroupConfigurationRef> endpointHelper;

    @PostConstruct
    public void init() {
        endpointHelper = new EndpointHelper<>(GroupConfiguration.class, provider);
    }

    @Override
    public Page<GroupConfiguration> getAll(PageParameters pageParams) {
        return endpointHelper.getAll(pageParams);
    }

    @Override
    public GroupConfiguration createNew(GroupConfiguration groupConfiguration) {
        return endpointHelper.create(groupConfiguration);
    }

    @Override
    public GroupConfiguration getSpecific(String id) {
        return endpointHelper.getSpecific(id);
    }

    @Override
    public void update(String id, GroupConfiguration buildConfigurationSet) {
        endpointHelper.update(id, buildConfigurationSet);
    }

    @Override
    public GroupConfiguration patchSpecific(String id, GroupConfiguration groupConfiguration) {
        return endpointHelper.update(id, groupConfiguration);
    }

    @Override
    public GroupBuild trigger(String id, GroupBuildParameters buildParams, GroupBuildRequest request) {
        return triggerBuild(id, Optional.ofNullable(request), buildParams);
    }

    @Override
    public Page<BuildConfiguration> getBuildConfigs(String id, PageParameters pageParams) {
        return buildConfigurationProvider.getBuildConfigurationsForGroup(
                pageParams.getPageIndex(),
                pageParams.getPageSize(),
                pageParams.getSort(),
                pageParams.getQ(),
                id);
    }

    @Override
    public void addBuildConfig(String id, BuildConfigurationRef buildConfig) {
        provider.addConfiguration(id, buildConfig.getId());
    }

    @Override
    public void removeBuildConfig(String id, String configId) {
        provider.removeConfiguration(id, configId);
    }

    @Override
    public Page<Build> getBuilds(String id, PageParameters pageParams, BuildsFilterParameters filterParams) {
        return buildProvider.getBuildsForGroupConfiguration(toBuildPageInfo(pageParams, filterParams), id);
    }

    @Override
    public Page<GroupBuild> getAllGroupBuilds(
            String id,
            PageParameters pageParams,
            GroupBuildsFilterParameters filterParams) {
        return groupBuildProvider.getGroupBuilds(GroupBuildPageInfo.toGroupBuildPageInfo(pageParams, filterParams), id);
    }

    private GroupBuild triggerBuild(
            String groupConfigId,
            Optional<GroupBuildRequest> buildConfigRevisions,
            GroupBuildParameters buildParams) {

        try {
            BuildOptions buildOptions = toBuildOptions(buildParams);

            String groupBuildId = buildTriggerer
                    .triggerGroupBuild(Integer.parseInt(groupConfigId), buildConfigRevisions, buildOptions);

            return groupBuildProvider.getSpecific(groupBuildId);
        } catch (BuildConflictException ex) {
            throw new AlreadyRunningException(ex, ex.getBuildTaskId());
        } catch (BuildRequestException ex) {
            throw new InvalidRequestException(ex);
        } catch (CoreException ex) {
            throw new RuntimeException(ex);
        }
    }

    private BuildOptions toBuildOptions(GroupBuildParameters buildParams) {
        BuildOptions buildOptions = new BuildOptions(
                buildParams.isTemporaryBuild(),
                false,
                false,
                false,
                buildParams.getRebuildMode(),
                buildParams.getAlignmentPreference());
        checkBuildOptionsValidity(buildOptions);
        return buildOptions;
    }

}
