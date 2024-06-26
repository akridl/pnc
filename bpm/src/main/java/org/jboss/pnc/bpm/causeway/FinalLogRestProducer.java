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
package org.jboss.pnc.bpm.causeway;

import org.jboss.pnc.api.bifrost.rest.FinalLogRest;
import org.jboss.pnc.common.json.GlobalModuleGroup;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

@ApplicationScoped
public class FinalLogRestProducer {
    @Inject
    private GlobalModuleGroup config;

    @Produces
    public FinalLogRest produce() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(config.getExternalBifrostUrl());
        ResteasyWebTarget rtarget = (ResteasyWebTarget) target;

        return rtarget.proxy(FinalLogRest.class);
    }
}
