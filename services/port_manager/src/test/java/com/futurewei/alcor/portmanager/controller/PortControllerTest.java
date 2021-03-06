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
package com.futurewei.alcor.portmanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.futurewei.alcor.portmanager.config.UnitTestConfig;
import com.futurewei.alcor.portmanager.repo.PortRepository;
import com.futurewei.alcor.web.entity.port.*;
import com.futurewei.alcor.web.entity.mac.*;
import com.futurewei.alcor.web.entity.subnet.SubnetWebJson;
import com.futurewei.alcor.web.entity.subnet.SubnetEntity;
import com.futurewei.alcor.web.entity.vpc.*;
import com.futurewei.alcor.web.entity.ip.*;
import com.futurewei.alcor.portmanager.restclient.IpManagerRestClient;
import com.futurewei.alcor.portmanager.restclient.MacManagerRestClient;
import com.futurewei.alcor.portmanager.restclient.SubnetManagerRestClient;
import com.futurewei.alcor.portmanager.restclient.VpcManagerRestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class PortControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    VpcManagerRestClient vpcManagerRestClient;

    @MockBean
    private SubnetManagerRestClient subnetManagerRestClient;

    @MockBean
    private IpManagerRestClient ipManagerRestClient;

    @MockBean
    private MacManagerRestClient macManagerRestClient;

    @MockBean
    private PortRepository portRepository;

    private String createPortUrl = "/project/" + UnitTestConfig.projectId + "/ports";
    private String updatePortUrl = createPortUrl + "/" + UnitTestConfig.portId;
    private String deletePortUrl = updatePortUrl;
    private String getPortUrl = updatePortUrl;
    private String listPortUrl = createPortUrl;

    private PortStateJson newPortStateJson() {
        List<PortState.FixedIp> fixedIps = new ArrayList<>();
        fixedIps.add(new PortState.FixedIp(UnitTestConfig.subnetId, UnitTestConfig.ip1));

        List<String> securityGroups = new ArrayList<>();
        securityGroups.add(UnitTestConfig.securityGroup);

        List<PortState.AllowAddressPair> allowedAddressPairs = new ArrayList<>();
        allowedAddressPairs.add(new PortState.AllowAddressPair(UnitTestConfig.ip2, UnitTestConfig.mac1));

        PortState portState = new PortState();
        portState.setId(UnitTestConfig.portId);
        portState.setVpcId(UnitTestConfig.vpcId);
        portState.setProjectId(UnitTestConfig.projectId);
        portState.setTenantId(UnitTestConfig.tenantId);
        portState.setFixedIps(fixedIps);
        portState.setMacAddress(UnitTestConfig.mac1);
        portState.setSecurityGroups(securityGroups);
        portState.setAllowedAddressPairs(allowedAddressPairs);

        return new PortStateJson(portState);
    }

    private IpAddrRequest newIpv4AddrRequest() {
        IpAddrRequest ipAddrRequest = new IpAddrRequest();
        ipAddrRequest.setRangeId(UnitTestConfig.rangeId);
        ipAddrRequest.setIpVersion(UnitTestConfig.ipv4Version);
        ipAddrRequest.setIp(UnitTestConfig.ip1);
        ipAddrRequest.setState(IpAddrState.ACTIVATED.getState());

        return ipAddrRequest;
    }

    private IpAddrRequest newIpv6AddrRequest() {
        IpAddrRequest ipAddrRequest = new IpAddrRequest();
        ipAddrRequest.setRangeId(UnitTestConfig.rangeId);
        ipAddrRequest.setIpVersion(UnitTestConfig.ipv6Version);
        ipAddrRequest.setIp(UnitTestConfig.ipv6Address);
        ipAddrRequest.setState(IpAddrState.ACTIVATED.getState());

        return ipAddrRequest;
    }

    private String newPortStateJsonStr() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(newPortStateJson());
    }

    private VpcWebJson newVpcStateJson() {
        VpcEntity vpcState = new VpcEntity();
        vpcState.setId(UnitTestConfig.vpcId);
        vpcState.setCidr(UnitTestConfig.vpcCidr);

        return new VpcWebJson(vpcState);
    }

    private SubnetWebJson newSubnetStateJson() {
        SubnetEntity subnetState = new SubnetEntity();
        subnetState.setProjectId(UnitTestConfig.projectId);
        subnetState.setId(UnitTestConfig.subnetId);
        subnetState.setName("subnet1");
        subnetState.setCidr(UnitTestConfig.vpcCidr);
        subnetState.setVpcId(UnitTestConfig.vpcId);
        subnetState.setIpV4RangeId(UnitTestConfig.rangeId);

        return new SubnetWebJson(subnetState);
    }

    private MacStateJson newMacStateJson() {
        MacState macState = new MacState();
        macState.setProjectId(UnitTestConfig.projectId);
        macState.setVpcId(UnitTestConfig.vpcId);
        macState.setPortId(UnitTestConfig.portId);
        macState.setMacAddress(UnitTestConfig.mac1);

        return new MacStateJson(macState);
    }

//    @Test
    public void createPortWithFixedIpsTest() throws Exception {
        Mockito.when(vpcManagerRestClient.verifyVpc(UnitTestConfig.projectId, UnitTestConfig.vpcId))
                .thenReturn(newVpcStateJson());

        Mockito.when(subnetManagerRestClient.getSubnetState(UnitTestConfig.projectId, UnitTestConfig.subnetId))
                .thenReturn(newSubnetStateJson());

        Mockito.when(ipManagerRestClient.allocateIpAddress(null, null, UnitTestConfig.rangeId, UnitTestConfig.ip1))
                .thenReturn(newIpv4AddrRequest());

        Mockito.when(macManagerRestClient.allocateMacAddress(UnitTestConfig.projectId, UnitTestConfig.vpcId, UnitTestConfig.portId, null))
                .thenReturn(newMacStateJson());

        Mockito.doNothing().when(portRepository);

        this.mockMvc.perform(post(createPortUrl).contentType(MediaType.APPLICATION_JSON)
                .content(UnitTestConfig.portStateWithFixedIps))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.port_state.id").value(UnitTestConfig.portId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.port_state.fixed_ips[0].ip_address").value(UnitTestConfig.ip1));
    }

//    @Test
    public void createPortWithoutFixedIpsTest() throws Exception {
        Mockito.when(vpcManagerRestClient.verifyVpc(UnitTestConfig.projectId, UnitTestConfig.vpcId))
                .thenReturn(newVpcStateJson());

        Mockito.when(subnetManagerRestClient.getSubnetState(UnitTestConfig.projectId, UnitTestConfig.subnetId))
                .thenReturn(newSubnetStateJson());

        Mockito.when(ipManagerRestClient.allocateIpAddress(IpVersion.IPV4, UnitTestConfig.vpcId, null, null))
                .thenReturn(newIpv4AddrRequest());

        Mockito.when(ipManagerRestClient.allocateIpAddress(IpVersion.IPV6, UnitTestConfig.vpcId, null, null))
                .thenReturn(newIpv6AddrRequest());

        Mockito.when(macManagerRestClient.allocateMacAddress(UnitTestConfig.projectId, UnitTestConfig.vpcId, UnitTestConfig.portId, null))
                .thenReturn(newMacStateJson());

        Mockito.doNothing().when(portRepository);

        this.mockMvc.perform(post(createPortUrl).contentType(MediaType.APPLICATION_JSON)
                .content(UnitTestConfig.portStateWithoutFixedIps))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.port_state.id").value(UnitTestConfig.portId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.port_state.fixed_ips[0].ip_address").value(UnitTestConfig.ip1));
    }

//    @Test
    public void createPortWithMacAddressTest() throws Exception {
        Mockito.when(vpcManagerRestClient.verifyVpc(UnitTestConfig.projectId, UnitTestConfig.vpcId))
                .thenReturn(newVpcStateJson());

        Mockito.when(subnetManagerRestClient.getSubnetState(UnitTestConfig.projectId, UnitTestConfig.subnetId))
                .thenReturn(newSubnetStateJson());

        Mockito.when(ipManagerRestClient.allocateIpAddress(null, null, UnitTestConfig.rangeId, UnitTestConfig.ip1))
                .thenReturn(newIpv4AddrRequest());

        Mockito.when(macManagerRestClient.allocateMacAddress(UnitTestConfig.projectId, UnitTestConfig.vpcId, UnitTestConfig.portId, UnitTestConfig.mac1))
                .thenReturn(newMacStateJson());

        Mockito.doNothing().when(portRepository);

        this.mockMvc.perform(post(createPortUrl).contentType(MediaType.APPLICATION_JSON)
                .content(UnitTestConfig.portStateWithMacAddress))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.port_state.id").value(UnitTestConfig.portId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.port_state.mac_address").value(UnitTestConfig.mac1));
    }

//    @Test
    public void createPortWithoutMacAddressTest() throws Exception {
        Mockito.when(vpcManagerRestClient.verifyVpc(UnitTestConfig.projectId, UnitTestConfig.vpcId))
                .thenReturn(newVpcStateJson());

        Mockito.when(subnetManagerRestClient.getSubnetState(UnitTestConfig.projectId, UnitTestConfig.subnetId))
                .thenReturn(newSubnetStateJson());

        Mockito.when(ipManagerRestClient.allocateIpAddress(null, null, UnitTestConfig.rangeId, UnitTestConfig.ip1))
                .thenReturn(newIpv4AddrRequest());

        Mockito.when(macManagerRestClient.allocateMacAddress(UnitTestConfig.projectId, UnitTestConfig.vpcId, UnitTestConfig.portId, null))
                .thenReturn(newMacStateJson());

        Mockito.doNothing().when(portRepository);

        this.mockMvc.perform(post(createPortUrl).contentType(MediaType.APPLICATION_JSON)
                .content(UnitTestConfig.portStateWithoutMacAddress))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.port_state.id").value(UnitTestConfig.portId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.port_state.mac_address").value(UnitTestConfig.mac1));
    }

//    @Test
    public void updateFixedIpsTest() throws Exception {
        Mockito.when(subnetManagerRestClient.getSubnetState(UnitTestConfig.projectId, UnitTestConfig.subnetId))
                .thenReturn(newSubnetStateJson());

        Mockito.when(ipManagerRestClient.allocateIpAddress(null, null, UnitTestConfig.rangeId, UnitTestConfig.ip1))
                .thenReturn(newIpv4AddrRequest());

        Mockito.when(ipManagerRestClient.allocateIpAddress(IpVersion.IPV4, UnitTestConfig.vpcId, null, null))
                .thenReturn(newIpv4AddrRequest());

        Mockito.when(portRepository.findItem(UnitTestConfig.portId))
                .thenReturn(newPortStateJson().getPortState());

        this.mockMvc.perform(put(updatePortUrl)
                .content(UnitTestConfig.updateFixedIps)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.port_state.id").value(UnitTestConfig.portId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.port_state.fixed_ips[0].ip_address").value(UnitTestConfig.ip2));
    }

//    @Test
    public void deleteFixedIpsTest() throws Exception {
        Mockito.when(subnetManagerRestClient.getSubnetState(UnitTestConfig.projectId, UnitTestConfig.subnetId))
                .thenReturn(newSubnetStateJson());

        Mockito.when(ipManagerRestClient.allocateIpAddress(null, null, UnitTestConfig.rangeId, UnitTestConfig.ip1))
                .thenReturn(newIpv4AddrRequest());

        Mockito.when(ipManagerRestClient.allocateIpAddress(IpVersion.IPV4, UnitTestConfig.vpcId, null, null))
                .thenReturn(newIpv4AddrRequest());

        Mockito.when(portRepository.findItem(UnitTestConfig.portId))
                .thenReturn(newPortStateJson().getPortState());

        this.mockMvc.perform(put(updatePortUrl)
                .content(UnitTestConfig.portStateWithoutFixedIps)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.port_state.id").value(UnitTestConfig.portId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.port_state.fixed_ips").isEmpty());

    }

    @Test
    public void getPortState() throws Exception {
        Mockito.when(portRepository.findItem(UnitTestConfig.portId))
                .thenReturn(newPortStateJson().getPortState());

        this.mockMvc.perform(get(getPortUrl))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.port.id")
                        .value(UnitTestConfig.portId)
                );
    }

    @Test
    public void listPortTest() throws Exception {
        Map<String, PortState> portStates = new HashMap<>();
        portStates.put(UnitTestConfig.portId, newPortStateJson().getPortState());

        Mockito.when(portRepository.findAllItems())
                .thenReturn(portStates);

        this.mockMvc.perform(get(listPortUrl))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$[0].port.id")
                        .value(UnitTestConfig.portId)
                );
    }

//    @Test
    public void deletePortTest() throws Exception {
        Mockito.when(subnetManagerRestClient.getSubnetState(UnitTestConfig.projectId, UnitTestConfig.subnetId))
                .thenReturn(newSubnetStateJson());

        Mockito.when(portRepository.findItem(UnitTestConfig.portId))
                .thenReturn(newPortStateJson().getPortState());

        this.mockMvc.perform(delete(deletePortUrl))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
