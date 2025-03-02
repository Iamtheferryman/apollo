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
package com.ctrip.framework.apollo.config.data.importer;

import org.springframework.core.env.MapPropertySource;

import java.util.Collections;

/**
 * @author vdisk <vdisk@foxmail.com>
 */
public class ApolloConfigEmptyPropertySource extends MapPropertySource {

    /**
     * Create a new {@code EmptyPropertySource} with the given name.
     *
     * @param name the associated name
     */
    public ApolloConfigEmptyPropertySource(String name) {
        super(name, Collections.emptyMap());
    }
}
