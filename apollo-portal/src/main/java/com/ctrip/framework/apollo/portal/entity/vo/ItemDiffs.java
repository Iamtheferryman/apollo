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
package com.ctrip.framework.apollo.portal.entity.vo;

import com.ctrip.framework.apollo.common.dto.ItemChangeSets;

public class ItemDiffs {
    private NamespaceIdentifier namespace;
    private ItemChangeSets diffs;
    private String extInfo;

    public ItemDiffs(NamespaceIdentifier namespace) {
        this.namespace = namespace;
    }

    public NamespaceIdentifier getNamespace() {
        return namespace;
    }

    public void setNamespace(NamespaceIdentifier namespace) {
        this.namespace = namespace;
    }

    public ItemChangeSets getDiffs() {
        return diffs;
    }

    public void setDiffs(ItemChangeSets diffs) {
        this.diffs = diffs;
    }

    public String getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }
}
