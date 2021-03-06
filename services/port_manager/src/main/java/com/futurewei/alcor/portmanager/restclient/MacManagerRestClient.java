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
package com.futurewei.alcor.portmanager.restclient;

import com.futurewei.alcor.web.entity.mac.MacState;
import com.futurewei.alcor.web.entity.mac.MacStateJson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;

@Configuration
public class MacManagerRestClient extends AbstractRestClient {
    @Value("${microservices.mac.service.url:#{\"\"}}")
    private String macManagerUrl;

    private void verifyAllocatedMacAddress(MacStateJson result) throws Exception {
        if (result == null || result.getMacState() == null ||
                result.getMacState().getMacAddress() == null ||
                result.getMacState().getMacAddress().isEmpty()) {
            throw new Exception("Verify allocated mac address failed");
        }
    }

    public void releaseMacAddress(String macAddress) throws Exception {
        String url = macManagerUrl + "/" + macAddress;

        restTemplate.delete(url);
    }

    public MacStateJson allocateMacAddress(String projectId, String vpcId, String portId, String macAddress) throws Exception {
        MacState macState = new MacState();
        macState.setProjectId(projectId);
        macState.setVpcId(vpcId);
        macState.setPortId(portId);
        macState.setMacAddress(macAddress);

        MacStateJson macStateJson = new MacStateJson();
        macStateJson.setMacState(macState);
        HttpEntity<MacStateJson> request = new HttpEntity<>(macStateJson);

        MacStateJson result = restTemplate.postForObject(macManagerUrl, request, MacStateJson.class);

        verifyAllocatedMacAddress(result);

        return result;
    }
}
