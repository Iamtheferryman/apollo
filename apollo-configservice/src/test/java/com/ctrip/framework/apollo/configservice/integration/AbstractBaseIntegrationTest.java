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
package com.ctrip.framework.apollo.configservice.integration;

import com.ctrip.framework.apollo.ConfigServiceTestConfiguration;
import com.ctrip.framework.apollo.biz.config.BizConfig;
import com.ctrip.framework.apollo.biz.entity.Namespace;
import com.ctrip.framework.apollo.biz.entity.Release;
import com.ctrip.framework.apollo.biz.entity.ReleaseMessage;
import com.ctrip.framework.apollo.biz.repository.ReleaseMessageRepository;
import com.ctrip.framework.apollo.biz.repository.ReleaseRepository;
import com.ctrip.framework.apollo.biz.service.BizDBPropertySource;
import com.ctrip.framework.apollo.biz.utils.ReleaseKeyGenerator;
import com.google.gson.Gson;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AbstractBaseIntegrationTest.TestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AbstractBaseIntegrationTest {
    private static final Gson GSON = new Gson();
    protected RestTemplate restTemplate = (new TestRestTemplate(new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(5)))).getRestTemplate();
    @Value("${local.server.port}")
    int port;
    @Autowired
    private ReleaseMessageRepository releaseMessageRepository;
    @Autowired
    private ReleaseRepository releaseRepository;

    @PostConstruct
    private void postConstruct() {
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
    }

    protected String getHostUrl() {
        return "localhost:" + port;
    }

    protected void sendReleaseMessage(String message) {
        ReleaseMessage releaseMessage = new ReleaseMessage(message);
        releaseMessageRepository.save(releaseMessage);
    }

    public Release buildRelease(String name, String comment, Namespace namespace,
                                Map<String, String> configurations, String owner) {
        Release release = new Release();
        release.setReleaseKey(ReleaseKeyGenerator.generateReleaseKey(namespace));
        release.setDataChangeCreatedTime(new Date());
        release.setDataChangeCreatedBy(owner);
        release.setDataChangeLastModifiedBy(owner);
        release.setName(name);
        release.setComment(comment);
        release.setAppId(namespace.getAppId());
        release.setClusterName(namespace.getClusterName());
        release.setNamespaceName(namespace.getNamespaceName());
        release.setConfigurations(GSON.toJson(configurations));
        release = releaseRepository.save(release);

        return release;
    }

    protected void periodicSendMessage(ExecutorService executorService, String message, AtomicBoolean stop) {
        executorService.submit(() -> {
            //wait for the request connected to server
            while (!stop.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                }

                //double check
                if (stop.get()) {
                    break;
                }

                sendReleaseMessage(message);
            }
        });
    }

    @Configuration
    @Import(ConfigServiceTestConfiguration.class)
    protected static class TestConfiguration {
        @Bean
        public BizConfig bizConfig(final BizDBPropertySource bizDBPropertySource) {
            return new TestBizConfig(bizDBPropertySource);
        }
    }

    private static class TestBizConfig extends BizConfig {
        public TestBizConfig(final BizDBPropertySource propertySource) {
            super(propertySource);
        }

        @Override
        public int appNamespaceCacheScanInterval() {
            //should be short enough to update the AppNamespace cache in time
            return 1;
        }

        @Override
        public TimeUnit appNamespaceCacheScanIntervalTimeUnit() {
            return TimeUnit.MILLISECONDS;
        }
    }
}
