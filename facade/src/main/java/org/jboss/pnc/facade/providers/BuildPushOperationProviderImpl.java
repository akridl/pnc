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
package org.jboss.pnc.facade.providers;

import org.jboss.pnc.dto.BuildPushOperation;
import org.jboss.pnc.facade.providers.api.BuildPushOperationProvider;
import org.jboss.pnc.mapper.api.BuildPushOperationMapper;
import org.jboss.pnc.spi.datastore.repositories.BuildPushOperationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.inject.Inject;

@PermitAll
@Stateless
public class BuildPushOperationProviderImpl
        extends OperationProviderImpl<org.jboss.pnc.model.BuildPushOperation, BuildPushOperation>
        implements BuildPushOperationProvider {

    private final Logger logger = LoggerFactory.getLogger(BuildPushOperationProviderImpl.class);

    @Inject
    public BuildPushOperationProviderImpl(BuildPushOperationRepository repository, BuildPushOperationMapper mapper) {
        super(repository, mapper, org.jboss.pnc.model.BuildPushOperation.class);
    }

}
