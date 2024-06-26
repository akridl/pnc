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
package org.jboss.pnc.integration.mock.client;

import org.jboss.pnc.api.bifrost.rest.FinalLogRest;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class FinalLogRestMock implements FinalLogRest {
    @Override
    public Response deleteFinalLog(String processContext) {
        return null;
    }

    @Override
    public Response getFinalLog(String processContext, String tag) {
        return null;
    }

    @Override
    public long getFinalLogSize(String buildId, String tag) {
        return 0;
    }

}
