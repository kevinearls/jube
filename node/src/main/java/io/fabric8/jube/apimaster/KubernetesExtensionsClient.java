/**
 *  Copyright 2005-2014 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.fabric8.jube.apimaster;

import io.fabric8.kubernetes.api.KubernetesClient;
import io.fabric8.kubernetes.api.KubernetesFactory;
import io.fabric8.kubernetes.api.model.Pod;

import javax.validation.constraints.NotNull;

/**
 * A simple client for working with {@link KubernetesExtensions}
 */
public class KubernetesExtensionsClient extends KubernetesClient {
    private KubernetesExtensions extensions;

    public KubernetesExtensionsClient() {
    }

    public KubernetesExtensionsClient(String url) {
        this(createJubeFactory(url));
    }

    public static KubernetesFactory createJubeFactory(String address) {
        return new KubernetesFactory(address, true, false);
    }

    public KubernetesExtensionsClient(KubernetesFactory factory) {
        super(factory, factory);
    }

    public KubernetesExtensions getExtensions() {
        if (extensions == null) {
            extensions = getFactory(true).createWebClient(KubernetesExtensions.class);
        }
        return extensions;
    }

    // Delegate API
    //-------------------------------------------------------------------------


    public String createLocalPod(Pod entity) throws Exception {
        return getExtensions().createLocalPod(entity);
    }

    public String deleteLocalPod(@NotNull String podId) throws Exception {
        return getExtensions().deleteLocalPod(podId);
    }
}
