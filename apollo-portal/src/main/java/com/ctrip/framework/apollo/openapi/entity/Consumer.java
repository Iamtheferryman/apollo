/*
 * Copyright 2021 Apollo Authors
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
 *
 */
package com.ctrip.framework.apollo.openapi.entity;

import com.ctrip.framework.apollo.common.entity.BaseEntity;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Consumer")
@SQLDelete(sql = "Update Consumer set isDeleted = 1 where id = ?")
@Where(clause = "isDeleted = 0")
public class Consumer extends BaseEntity {

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "AppId", nullable = false)
    private String appId;

    @Column(name = "OrgId", nullable = false)
    private String orgId;

    @Column(name = "OrgName", nullable = false)
    private String orgName;

    @Column(name = "OwnerName", nullable = false)
    private String ownerName;

    @Column(name = "OwnerEmail", nullable = false)
    private String ownerEmail;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public String toString() {
        return toStringHelper().add("name", name).add("appId", appId)
                .add("orgId", orgId)
                .add("orgName", orgName)
                .add("ownerName", ownerName)
                .add("ownerEmail", ownerEmail).toString();
    }
}
