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
package com.ctrip.framework.foundation.internals.provider;

import com.ctrip.framework.foundation.internals.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

import static com.ctrip.framework.foundation.internals.provider.DefaultServerProvider.DEFAULT_SERVER_PROPERTIES_PATH_ON_LINUX;
import static com.ctrip.framework.foundation.internals.provider.DefaultServerProvider.DEFAULT_SERVER_PROPERTIES_PATH_ON_WINDOWS;
import static org.junit.Assert.*;

public class DefaultServerProviderTest {
    private DefaultServerProvider defaultServerProvider;

    @Before
    public void setUp() throws Exception {
        cleanUp();
        defaultServerProvider = new DefaultServerProvider();
    }

    @After
    public void tearDown() throws Exception {
        cleanUp();
    }

    private void cleanUp() {
        System.clearProperty("env");
        System.clearProperty("idc");
        System.clearProperty("apollo.path.server.properties");
    }

    @Test
    public void testGetServerPropertiesPathDefault() {
        assertEquals(Utils.isOSWindows() ? DEFAULT_SERVER_PROPERTIES_PATH_ON_WINDOWS
                : DEFAULT_SERVER_PROPERTIES_PATH_ON_LINUX, defaultServerProvider.getServerPropertiesPath());
    }

    @Test
    public void testGetServerPropertiesPathCustom() {
        final String customPath = "/simple/custom/path";
        System.setProperty("apollo.path.server.properties", customPath);
        assertEquals(customPath, defaultServerProvider.getServerPropertiesPath());
    }

    @Test
    public void testEnvWithSystemProperty() throws Exception {
        String someEnv = "someEnv";
        String someDc = "someDc";
        System.setProperty("env", someEnv);
        System.setProperty("idc", someDc);

        defaultServerProvider.initialize(null);

        assertEquals(someEnv, defaultServerProvider.getEnvType());
        assertEquals(someDc, defaultServerProvider.getDataCenter());
    }

    @Test
    public void testWithPropertiesStream() throws Exception {
        File baseDir = new File("src/test/resources/properties");
        File serverProperties = new File(baseDir, "server.properties");
        defaultServerProvider.initialize(new FileInputStream(serverProperties));

        assertEquals("SHAJQ", defaultServerProvider.getDataCenter());
        assertTrue(defaultServerProvider.isEnvTypeSet());
        assertEquals("DEV", defaultServerProvider.getEnvType());
    }

    @Test
    public void testWithUTF8BomPropertiesStream() throws Exception {
        File baseDir = new File("src/test/resources/properties");
        File serverProperties = new File(baseDir, "server-with-utf8bom.properties");
        defaultServerProvider.initialize(new FileInputStream(serverProperties));

        assertEquals("SHAJQ", defaultServerProvider.getDataCenter());
        assertTrue(defaultServerProvider.isEnvTypeSet());
        assertEquals("DEV", defaultServerProvider.getEnvType());
    }

    @Test
    public void testWithPropertiesStreamAndEnvFromSystemProperty() throws Exception {
        String prodEnv = "pro";
        System.setProperty("env", prodEnv);

        File baseDir = new File("src/test/resources/properties");
        File serverProperties = new File(baseDir, "server.properties");
        defaultServerProvider.initialize(new FileInputStream(serverProperties));

        String predefinedDataCenter = "SHAJQ";

        assertEquals(predefinedDataCenter, defaultServerProvider.getDataCenter());
        assertTrue(defaultServerProvider.isEnvTypeSet());
        assertEquals(prodEnv, defaultServerProvider.getEnvType());
    }

    @Test
    public void testWithNoPropertiesStream() throws Exception {
        defaultServerProvider.initialize(null);

        assertNull(defaultServerProvider.getDataCenter());
        assertFalse(defaultServerProvider.isEnvTypeSet());
        assertNull(defaultServerProvider.getEnvType());
    }
}
