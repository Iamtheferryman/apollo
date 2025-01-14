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

import com.ctrip.framework.apollo.common.dto.ClusterDTO;
import com.ctrip.framework.apollo.common.entity.App;
import com.ctrip.framework.apollo.common.entity.AppNamespace;
import com.ctrip.framework.apollo.common.exception.BadRequestException;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.ctrip.framework.apollo.core.utils.StringUtils;
import com.ctrip.framework.apollo.portal.controller.ConfigsImportController;
import com.ctrip.framework.apollo.portal.environment.Env;
import com.google.common.base.Splitter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/**
 * First version: move from {@link ConfigsImportController#importConfigFile(java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.springframework.web.multipart.MultipartFile)}
 *
 * @author wxq
 */
public class ConfigFileUtils {

    public static final String APP_METADATA_FILENAME = "app.metadata";
    public static final String CLUSTER_METADATA_FILE_SUFFIX = ".cluster.metadata";
    public static final String APP_NAMESPACE_METADATA_FILE_SUFFIX = ".appnamespace.metadata";

    public static void check(MultipartFile file) {
        checkEmpty(file);
        final String originalFilename = file.getOriginalFilename();
        checkFormat(originalFilename);
    }

    /**
     * @throws BadRequestException if file is empty
     */
    static void checkEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("The file is empty. " + file.getOriginalFilename());
        }
    }

    /**
     * @throws BadRequestException if file's format is invalid
     */
    static void checkFormat(final String originalFilename) {
        final List<String> fileNameSplit = Splitter.on(".").splitToList(originalFilename);
        if (fileNameSplit.size() <= 1) {
            throw new BadRequestException("The file format is invalid.");
        }

        for (String s : fileNameSplit) {
            if (StringUtils.isEmpty(s)) {
                throw new BadRequestException("The file format is invalid.");
            }
        }
    }

    static String[] getThreePart(final String originalFilename) {
        return originalFilename.split("[+]");
    }

    /**
     * @throws BadRequestException if file's name cannot divide to 3 parts by "+" symbol
     */
    static void checkThreePart(final String originalFilename) {
        String[] parts = getThreePart(originalFilename);
        if (3 != parts.length) {
            throw new BadRequestException("file name [" + originalFilename + "] not valid");
        }
    }

    /**
     * <pre>
     *  "application+default+application.properties" -> "properties"
     *  "application+default+application.yml" -> "yml"
     * </pre>
     *
     * @throws BadRequestException if file's format is invalid
     */
    public static String getFormat(final String originalFilename) {
        final List<String> fileNameSplit = Splitter.on(".").splitToList(originalFilename);
        if (fileNameSplit.size() <= 1) {
            throw new BadRequestException("The file format is invalid.");
        }
        return fileNameSplit.get(fileNameSplit.size() - 1);
    }

    /**
     * <pre>
     *  "123+default+application.properties" -> "123"
     *  "abc+default+application.yml" -> "abc"
     *  "666+default+application.json" -> "666"
     * </pre>
     *
     * @throws BadRequestException if file's name is invalid
     */
    public static String getAppId(final String originalFilename) {
        checkThreePart(originalFilename);
        return getThreePart(originalFilename)[0];
    }

    public static String getClusterName(final String originalFilename) {
        checkThreePart(originalFilename);
        return getThreePart(originalFilename)[1];
    }

    /**
     * <pre>
     *  "application+default+application.properties" -> "application"
     *  "application+default+application.yml" -> "application.yml"
     *  "application+default+application.json" -> "application.json"
     *  "application+default+application.333.yml" -> "application.333.yml"
     * </pre>
     *
     * @throws BadRequestException if file's name is invalid
     */
    public static String getNamespace(final String originalFilename) {
        checkThreePart(originalFilename);
        final String[] threeParts = getThreePart(originalFilename);
        final String suffix = threeParts[2];
        if (!suffix.contains(".")) {
            throw new BadRequestException(originalFilename + " namespace and format is invalid!");
        }
        final int lastDotIndex = suffix.lastIndexOf(".");
        final String namespace = suffix.substring(0, lastDotIndex);
        // format after last character '.'
        final String format = suffix.substring(lastDotIndex + 1);
        if (!ConfigFileFormat.isValidFormat(format)) {
            throw new BadRequestException(originalFilename + " format is invalid!");
        }
        ConfigFileFormat configFileFormat = ConfigFileFormat.fromString(format);
        if (configFileFormat.equals(ConfigFileFormat.Properties)) {
            return namespace;
        } else {
            // compatibility of other format
            return namespace + "." + format;
        }
    }

    /**
     * <pre>
     *   appId    cluster   namespace       return
     *   666      default   application     666+default+application.properties
     *   123      none      action.yml      123+none+action.yml
     * </pre>
     */
    public static String toFilename(
            final String appId,
            final String clusterName,
            final String namespace,
            final ConfigFileFormat configFileFormat
    ) {
        final String suffix;
        if (ConfigFileFormat.Properties.equals(configFileFormat)) {
            suffix = "." + ConfigFileFormat.Properties.getValue();
        } else {
            suffix = "";
        }
        return appId + "+" + clusterName + "+" + namespace + suffix;
    }

    /**
     * file path = ownerName/appId/env/configFilename
     *
     * @return file path in compressed file
     */
    public static String genNamespacePath(
            final String ownerName,
            final String appId,
            final Env env,
            final String configFilename
    ) {
        return String.join(File.separator, ownerName, appId, env.getName(), configFilename);
    }

    /**
     * path = ownerName/appId/app.metadata
     */
    public static String genAppInfoPath(App app) {
        return String.join(File.separator, app.getOwnerName(), app.getAppId(), APP_METADATA_FILENAME);
    }

    /**
     * path = {appNamespace}.appnamespace.metadata
     */
    public static String genAppNamespaceInfoPath(AppNamespace appNamespace) {
        return String.join(File.separator,
                appNamespace.getAppId() + "+" + appNamespace.getName() + APP_NAMESPACE_METADATA_FILE_SUFFIX);
    }

    /**
     * path = ownerName/appId/env/${clusterName}.metadata
     */
    public static String genClusterInfoPath(App app, Env env, ClusterDTO cluster) {
        return String.join(File.separator, app.getOwnerName(), app.getAppId(), env.getName(),
                cluster.getName() + CLUSTER_METADATA_FILE_SUFFIX);
    }
}
