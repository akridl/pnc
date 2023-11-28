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
package org.jboss.pnc.mock.repository;

import org.jboss.pnc.model.BuildConfigurationSet;
import org.jboss.pnc.spi.datastore.repositories.BuildConfigurationSetRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import java.util.List;
import java.util.stream.Collectors;

@Alternative
@ApplicationScoped
public class BuildConfigurationSetRepositoryMock extends IntIdRepositoryMock<BuildConfigurationSet>
        implements BuildConfigurationSetRepository {

    @Override
    public List<BuildConfigurationSet> withProductVersionId(Integer id) {
        return data.stream()
                .filter(bcs -> bcs.getProductVersion() != null && bcs.getProductVersion().getId().equals(id))
                .collect(Collectors.toList());
    }
}