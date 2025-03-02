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
package com.ctrip.framework.apollo.biz.repository;

import com.ctrip.framework.apollo.biz.AbstractIntegrationTest;
import com.ctrip.framework.apollo.common.entity.App;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AppRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private AppRepository appRepository;

    @Test
    public void testCreate() {
        String appId = "someAppId";
        String appName = "someAppName";
        String ownerName = "someOwnerName";
        String ownerEmail = "someOwnerName@ctrip.com";

        App app = new App();
        app.setAppId(appId);
        app.setName(appName);
        app.setOwnerName(ownerName);
        app.setOwnerEmail(ownerEmail);

        Assert.assertEquals(0, appRepository.count());

        appRepository.save(app);

        Assert.assertEquals(1, appRepository.count());
    }

    @Test
    public void testRemove() {
        String appId = "someAppId";
        String appName = "someAppName";
        String ownerName = "someOwnerName";
        String ownerEmail = "someOwnerName@ctrip.com";

        App app = new App();
        app.setAppId(appId);
        app.setName(appName);
        app.setOwnerName(ownerName);
        app.setOwnerEmail(ownerEmail);

        Assert.assertEquals(0, appRepository.count());

        appRepository.save(app);

        Assert.assertEquals(1, appRepository.count());

        appRepository.deleteById(app.getId());

        Assert.assertEquals(0, appRepository.count());
    }

}
