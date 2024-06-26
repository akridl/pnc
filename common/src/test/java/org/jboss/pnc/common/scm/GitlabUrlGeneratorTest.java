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
package org.jboss.pnc.common.scm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GitlabUrlGeneratorTest {

    private final ScmUrlGenerator scmUrlGenerator = new GitlabScmUrlGenerator();

    @Test
    public void downloadUrlShouldHandleHttpCloneLink() throws ScmException {
        String url = "https://localhost.com/workspace/project/repository.git";
        String downloadUrl = "https://localhost.com/workspace/project/repository/-/archive/abcde/sources.tar.gz";
        String ref = "abcde";

        assertEquals(scmUrlGenerator.generateTarballDownloadUrl(url, ref), downloadUrl);
    }

}
