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
package com.ctrip.framework.apollo.openapi.service;

import com.ctrip.framework.apollo.portal.AbstractIntegrationTest;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author wxq
 */
public class ConsumerServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ConsumerService consumerService;

    @Test
    @Sql(scripts = "/sql/openapi/ConsumerServiceIntegrationTest.testFindAppIdsAuthorizedByConsumerId.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testFindAppIdsAuthorizedByConsumerId() {
        Set<String> appIds = this.consumerService.findAppIdsAuthorizedByConsumerId(1000L);
        assertEquals(Sets.newHashSet("consumer-test-app-id-0", "consumer-test-app-id-1"), appIds);
        assertFalse(appIds.contains("consumer-test-app-id-2"));
    }
}
