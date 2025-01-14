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
package com.ctrip.framework.apollo.portal.spi.oidc;

import com.ctrip.framework.apollo.portal.entity.bo.UserInfo;
import com.ctrip.framework.apollo.portal.spi.UserService;

/**
 * @author vdisk <vdisk@foxmail.com>
 */
public interface OidcLocalUserService extends UserService {

    /**
     * create local user info related to the oidc user
     *
     * @param newUserInfo the oidc user's info
     */
    void createLocalUser(UserInfo newUserInfo);

    /**
     * update user's info
     *
     * @param newUserInfo the new user's info
     */
    void updateUserInfo(UserInfo newUserInfo);
}
