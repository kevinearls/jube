/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jimagezip.local;

import io.fabric8.common.util.Objects;
import io.fabric8.kubernetes.api.Kubernetes;
import io.fabric8.kubernetes.api.KubernetesHelper;
import io.fabric8.kubernetes.api.model.CurrentState;
import io.fabric8.kubernetes.api.model.DesiredState;
import io.fabric8.kubernetes.api.model.ManifestContainer;
import io.fabric8.kubernetes.api.model.PodListSchema;
import io.fabric8.kubernetes.api.model.PodSchema;
import io.fabric8.kubernetes.api.model.ReplicationControllerListSchema;
import io.fabric8.kubernetes.api.model.ReplicationControllerSchema;
import io.fabric8.kubernetes.api.model.ServiceListSchema;
import io.fabric8.kubernetes.api.model.ServiceSchema;
import io.hawt.util.Strings;
import io.jimagezip.process.ProcessManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Map;

import static io.jimagezip.local.NodeHelper.getOrCreateCurrentState;

/**
 * Implements the local node controller
 */
@Singleton
@Path("api/v1beta1")
public class LocalNodeController implements Kubernetes {
    private final ProcessManager processManager;
    private final LocalNodeModel model;
    private final ReplicationManager replicationManager;
    private final ProcessMonitor processMonitor;

    @Inject
    public LocalNodeController(ProcessManager processManager, LocalNodeModel model, ReplicationManager replicationManager, ProcessMonitor processMonitor) {
        this.processManager = processManager;
        this.model = model;
        this.replicationManager = replicationManager;
        this.processMonitor = processMonitor;
    }

    @Override
    public PodListSchema getPods() {
        return model.getPods();
    }

    @Override
    public PodSchema getPod(@NotNull String podId) {
        Map<String, PodSchema> map = model.getPodMap();
        return map.get(podId);
    }

    @Override
    public String createPod(PodSchema pod) throws Exception {
        String id = pod.getId();
        if (Strings.isBlank(id)) {
            id = model.createID("Pod");
            pod.setId(id);
        }
        return updatePod(id, pod);
    }


    @Override
    public String updatePod(@NotNull String podId, PodSchema pod) throws Exception {
        System.out.println("Updating pod " + pod);
        DesiredState desiredState = pod.getDesiredState();
        Objects.notNull(desiredState, "desiredState");

        CurrentState currentState = getOrCreateCurrentState(pod);
        List<ManifestContainer> containers = KubernetesHelper.getContainers(pod);
        model.updatePod(podId, pod);
        return NodeHelper.createMissingContainers(processManager, pod, currentState, containers);
    }

    @Override
    public String deletePod(@NotNull String podId) throws Exception {
        PodSchema pod = model.getPod(podId);
        if (pod != null) {
            // TODO delete containers
        }
        return null;
    }

    @Override
    public ServiceListSchema getServices() {
        return model.getServices();
    }

    @Override
    public ServiceSchema getService(@NotNull String serviceId) {
        Map<String, ServiceSchema> map = model.getServiceMap();
        return map.get(serviceId);
    }

    @Override
    public String createService(ServiceSchema entity) throws Exception {
        // TODO
        return null;
    }

    @Override
    public String updateService(@NotNull String serviceId, ServiceSchema entity) throws Exception {
        // TODO
        return null;
    }

    @Override
    public String deleteService(@NotNull String serviceId) throws Exception {
        // TODO
        return null;
    }

    @Override
    public ReplicationControllerListSchema getReplicationControllers() {
        return model.getReplicationControllers();
    }

    @Override
    public ReplicationControllerSchema getReplicationController(@NotNull String controllerId) {
        Map<String, ReplicationControllerSchema> map = KubernetesHelper.getReplicationControllerMap(this);
        return map.get(controllerId);
    }

    @Override
    public String createReplicationController(ReplicationControllerSchema replicationController) throws Exception {
        String id = replicationController.getId();
        if (Strings.isBlank(id)) {
            id = model.createID("ReplicationController");
            replicationController.setId(id);
        }
        return updateReplicationController(id, replicationController);
    }

    @Override
    public String updateReplicationController(@NotNull String controllerId, ReplicationControllerSchema replicationController) throws Exception {
        System.out.println("Updating pod " + controllerId);
        model.updateReplicationController(controllerId, replicationController);
        return null;
    }

    @Override
    public String deleteReplicationController(@NotNull String controllerId) throws Exception {
        // TODO
        return null;
    }
}