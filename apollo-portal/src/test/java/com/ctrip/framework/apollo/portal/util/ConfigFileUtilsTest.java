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
package com.ctrip.framework.apollo.portal.util;

import com.ctrip.framework.apollo.common.exception.BadRequestException;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class ConfigFileUtilsTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void checkFormat() {
        ConfigFileUtils.checkFormat("1234+default+app.properties");
        ConfigFileUtils.checkFormat("1234+default+app.yml");
        ConfigFileUtils.checkFormat("1234+default+app.json");
    }

    @Test(expected = BadRequestException.class)
    public void checkFormatWithException0() {
        ConfigFileUtils.checkFormat("1234+defaultes");
    }

    @Test(expected = BadRequestException.class)
    public void checkFormatWithException1() {
        ConfigFileUtils.checkFormat(".json");
    }

    @Test(expected = BadRequestException.class)
    public void checkFormatWithException2() {
        ConfigFileUtils.checkFormat("application.");
    }

    @Test
    public void getFormat() {
        final String properties = ConfigFileUtils.getFormat("application+default+application.properties");
        assertEquals("properties", properties);

        final String yml = ConfigFileUtils.getFormat("application+default+application.yml");
        assertEquals("yml", yml);
    }

    @Test
    public void getAppId() {
        final String application = ConfigFileUtils.getAppId("application+default+application.properties");
        assertEquals("application", application);

        final String abc = ConfigFileUtils.getAppId("abc+default+application.yml");
        assertEquals("abc", abc);
    }

    @Test
    public void getClusterName() {
        final String cluster = ConfigFileUtils.getClusterName("application+default+application.properties");
        assertEquals("default", cluster);

        final String Beijing = ConfigFileUtils.getClusterName("abc+Beijing+application.yml");
        assertEquals("Beijing", Beijing);
    }

    @Test
    public void getNamespace() {
        final String application = ConfigFileUtils.getNamespace("234+default+application.properties");
        assertEquals("application", application);

        final String applicationYml = ConfigFileUtils.getNamespace("abc+default+application.yml");
        assertEquals("application.yml", applicationYml);
    }

    @Test
    public void toFilename() {
        final String propertiesFilename0 = ConfigFileUtils.toFilename("123", "default", "application", ConfigFileFormat.Properties);
        logger.info("propertiesFilename0 {}", propertiesFilename0);
        assertEquals("123+default+application.properties", propertiesFilename0);

        final String ymlFilename0 = ConfigFileUtils.toFilename("666", "none", "cc.yml", ConfigFileFormat.YML);
        logger.info("ymlFilename0 {}", ymlFilename0);
        assertEquals("666+none+cc.yml", ymlFilename0);
    }

}
