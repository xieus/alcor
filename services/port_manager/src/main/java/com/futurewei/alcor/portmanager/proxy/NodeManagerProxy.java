/*
Copyright 2019 The Alcor Authors.

Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/
package com.futurewei.alcor.portmanager.proxy;

import com.futurewei.alcor.portmanager.util.SpringContextUtil;
import com.futurewei.alcor.portmanager.rollback.PortStateRollback;
import com.futurewei.alcor.web.entity.NodeInfoJson;
import com.futurewei.alcor.portmanager.restclient.NodeManagerRestClient;
import java.util.Stack;

public class NodeManagerProxy {
    private NodeManagerRestClient nodeManagerRestClient;
    private Stack<PortStateRollback> rollbacks;

    public NodeManagerProxy(Stack<PortStateRollback> rollbacks) {
        nodeManagerRestClient = SpringContextUtil.getBean(NodeManagerRestClient.class);
        this.rollbacks = rollbacks;
    }

    /**
     * Verify if the host/node of nodeId exists
     * @param nodeId Id of host/node
     * @return The information of host/node
     * @throws Exception Rest request exception
     */
    public NodeInfoJson verifyHost(String nodeId) throws Exception {
        return nodeManagerRestClient.getNodeInfo(nodeId);
    }
}
