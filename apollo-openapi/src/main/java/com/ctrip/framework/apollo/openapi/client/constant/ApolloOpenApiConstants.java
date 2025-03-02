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
package com.ctrip.framework.apollo.openapi.client.constant;

public interface ApolloOpenApiConstants {
    int DEFAULT_CONNECT_TIMEOUT = 1000; //1 second
    int DEFAULT_READ_TIMEOUT = 5000; //5 seconds
    String OPEN_API_V1_PREFIX = "/openapi/v1";
    String JSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

}
