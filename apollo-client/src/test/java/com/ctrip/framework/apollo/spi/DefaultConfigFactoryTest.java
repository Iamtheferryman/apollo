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
package com.ctrip.framework.apollo.spi;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.build.MockInjector;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.internals.*;
import com.ctrip.framework.apollo.util.ConfigUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class DefaultConfigFactoryTest {
    private static String someAppId;
    private static Env someEnv;
    private DefaultConfigFactory defaultConfigFactory;

    @Before
    public void setUp() throws Exception {
        someAppId = "someId";
        someEnv = Env.DEV;
        MockInjector.setInstance(ConfigUtil.class, new MockConfigUtil());
        defaultConfigFactory = spy(new DefaultConfigFactory());
    }

    @After
    public void tearDown() throws Exception {
        MockInjector.reset();
    }

    @Test
    public void testCreate() throws Exception {
        String someNamespace = "someName";
        Properties someProperties = new Properties();
        String someKey = "someKey";
        String someValue = "someValue";
        someProperties.setProperty(someKey, someValue);

        LocalFileConfigRepository someLocalConfigRepo = mock(LocalFileConfigRepository.class);
        when(someLocalConfigRepo.getConfig()).thenReturn(someProperties);

        doReturn(someLocalConfigRepo).when(defaultConfigFactory).createLocalConfigRepository(someNamespace);

        Config result = defaultConfigFactory.create(someNamespace);

        assertThat("DefaultConfigFactory should create DefaultConfig", result,
                is(instanceOf(DefaultConfig.class)));
        assertEquals(someValue, result.getProperty(someKey, null));
    }

    @Test
    public void testCreateLocalConfigRepositoryInLocalDev() throws Exception {
        String someNamespace = "someName";
        someEnv = Env.LOCAL;

        LocalFileConfigRepository localFileConfigRepository =
                defaultConfigFactory.createLocalConfigRepository(someNamespace);

        assertNull(ReflectionTestUtils.getField(localFileConfigRepository, "m_upstream"));
    }

    @Test
    public void testCreatePropertiesCompatibleFileConfigRepository() throws Exception {
        ConfigFileFormat somePropertiesCompatibleFormat = ConfigFileFormat.YML;
        String someNamespace = "someName" + "." + somePropertiesCompatibleFormat;
        Properties someProperties = new Properties();
        String someKey = "someKey";
        String someValue = "someValue";
        someProperties.setProperty(someKey, someValue);

        PropertiesCompatibleFileConfigRepository someRepository = mock(PropertiesCompatibleFileConfigRepository.class);
        when(someRepository.getConfig()).thenReturn(someProperties);

        doReturn(someRepository).when(defaultConfigFactory)
                .createPropertiesCompatibleFileConfigRepository(someNamespace, somePropertiesCompatibleFormat);

        Config result = defaultConfigFactory.create(someNamespace);

        assertThat("DefaultConfigFactory should create DefaultConfig", result,
                is(instanceOf(DefaultConfig.class)));
        assertEquals(someValue, result.getProperty(someKey, null));
    }

    @Test
    public void testCreateConfigFile() throws Exception {
        String someNamespace = "someName";
        String anotherNamespace = "anotherName";
        String yetAnotherNamespace = "yetAnotherNamespace";
        Properties someProperties = new Properties();

        LocalFileConfigRepository someLocalConfigRepo = mock(LocalFileConfigRepository.class);
        when(someLocalConfigRepo.getConfig()).thenReturn(someProperties);

        doReturn(someLocalConfigRepo).when(defaultConfigFactory).createLocalConfigRepository(someNamespace);
        doReturn(someLocalConfigRepo).when(defaultConfigFactory).createLocalConfigRepository(anotherNamespace);
        doReturn(someLocalConfigRepo).when(defaultConfigFactory).createLocalConfigRepository(yetAnotherNamespace);

        ConfigFile propertyConfigFile =
                defaultConfigFactory.createConfigFile(someNamespace, ConfigFileFormat.Properties);
        ConfigFile xmlConfigFile =
                defaultConfigFactory.createConfigFile(anotherNamespace, ConfigFileFormat.XML);
        ConfigFile jsonConfigFile =
                defaultConfigFactory.createConfigFile(yetAnotherNamespace, ConfigFileFormat.JSON);
        ConfigFile ymlConfigFile = defaultConfigFactory.createConfigFile(someNamespace,
                ConfigFileFormat.YML);
        ConfigFile yamlConfigFile = defaultConfigFactory.createConfigFile(someNamespace,
                ConfigFileFormat.YAML);

        assertThat("Should create PropertiesConfigFile for properties format", propertyConfigFile, is(instanceOf(
                PropertiesConfigFile.class)));
        assertEquals(someNamespace, propertyConfigFile.getNamespace());

        assertThat("Should create XmlConfigFile for xml format", xmlConfigFile, is(instanceOf(
                XmlConfigFile.class)));
        assertEquals(anotherNamespace, xmlConfigFile.getNamespace());

        assertThat("Should create JsonConfigFile for json format", jsonConfigFile, is(instanceOf(
                JsonConfigFile.class)));
        assertEquals(yetAnotherNamespace, jsonConfigFile.getNamespace());

        assertThat("Should create YmlConfigFile for yml format", ymlConfigFile, is(instanceOf(
                YmlConfigFile.class)));
        assertEquals(someNamespace, ymlConfigFile.getNamespace());

        assertThat("Should create YamlConfigFile for yaml format", yamlConfigFile, is(instanceOf(
                YamlConfigFile.class)));
        assertEquals(someNamespace, yamlConfigFile.getNamespace());

    }

    @Test
    public void testDetermineFileFormat() throws Exception {
        checkFileFormat("abc", ConfigFileFormat.Properties);
        checkFileFormat("abc.properties", ConfigFileFormat.Properties);
        checkFileFormat("abc.pRopErties", ConfigFileFormat.Properties);
        checkFileFormat("abc.xml", ConfigFileFormat.XML);
        checkFileFormat("abc.xmL", ConfigFileFormat.XML);
        checkFileFormat("abc.json", ConfigFileFormat.JSON);
        checkFileFormat("abc.jsOn", ConfigFileFormat.JSON);
        checkFileFormat("abc.yaml", ConfigFileFormat.YAML);
        checkFileFormat("abc.yAml", ConfigFileFormat.YAML);
        checkFileFormat("abc.yml", ConfigFileFormat.YML);
        checkFileFormat("abc.yMl", ConfigFileFormat.YML);
        checkFileFormat("abc.properties.yml", ConfigFileFormat.YML);
    }

    @Test
    public void testTrimNamespaceFormat() throws Exception {
        checkNamespaceName("abc", ConfigFileFormat.Properties, "abc");
        checkNamespaceName("abc.properties", ConfigFileFormat.Properties, "abc");
        checkNamespaceName("abcproperties", ConfigFileFormat.Properties, "abcproperties");
        checkNamespaceName("abc.pRopErties", ConfigFileFormat.Properties, "abc");
        checkNamespaceName("abc.xml", ConfigFileFormat.XML, "abc");
        checkNamespaceName("abc.xmL", ConfigFileFormat.XML, "abc");
        checkNamespaceName("abc.json", ConfigFileFormat.JSON, "abc");
        checkNamespaceName("abc.jsOn", ConfigFileFormat.JSON, "abc");
        checkNamespaceName("abc.yaml", ConfigFileFormat.YAML, "abc");
        checkNamespaceName("abc.yAml", ConfigFileFormat.YAML, "abc");
        checkNamespaceName("abc.yml", ConfigFileFormat.YML, "abc");
        checkNamespaceName("abc.yMl", ConfigFileFormat.YML, "abc");
        checkNamespaceName("abc.proPerties.yml", ConfigFileFormat.YML, "abc.proPerties");
    }

    private void checkFileFormat(String namespaceName, ConfigFileFormat expectedFormat) {
        assertEquals(expectedFormat, defaultConfigFactory.determineFileFormat(namespaceName));
    }

    private void checkNamespaceName(String namespaceName, ConfigFileFormat format, String expectedNamespaceName) {
        assertEquals(expectedNamespaceName, defaultConfigFactory.trimNamespaceFormat(namespaceName, format));
    }

    public static class MockConfigUtil extends ConfigUtil {
        @Override
        public String getAppId() {
            return someAppId;
        }

        @Override
        public Env getApolloEnv() {
            return someEnv;
        }
    }

}
