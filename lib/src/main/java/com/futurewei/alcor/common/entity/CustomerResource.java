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

package com.futurewei.alcor.common.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class CustomerResource extends Resource {

    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("tenant_id")
    private String tenantId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    public CustomerResource() {

    }

    public CustomerResource(CustomerResource state) {
        this(state.projectId, state.tenantId, state.getId(), state.name, state.description);
    }

    public CustomerResource(String projectId, String id, String name, String description) {
        super(id);
        this.projectId = projectId;
        this.tenantId = this.projectId;
        this.name = name;
        this.description = description;
    }

    public CustomerResource(String projectId, String tenantId, String id, String name, String description) {
        super(id);
        this.projectId = projectId;
        this.tenantId = tenantId;
        this.name = name;
        this.description = description;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTenantId() {
        return this.tenantId == null ? this.projectId : this.tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        CustomerResource that = (CustomerResource) o;
        return Objects.equals(this.projectId, that.projectId) &&
                Objects.equals(this.tenantId, that.getTenantId()) &&
                Objects.equals(this.getId(), that.getId()) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.projectId, this.tenantId, this.getId(), this.name, this.description);
    }

    @Override
    public String toString() {
        return "CustomerResource{" +
                "projectId='" + this.projectId + '\'' +
                "tenantId='" + this.tenantId + '\'' +
                ", id='" + this.getId() + '\'' +
                ", name='" + this.name + '\'' +
                ", description='" + this.description + '\'' +
                '}';
    }
}
