/**
 * Copyright (c) 2016 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.trustedanalytics.modelcatalog.rest.client.configuration;

import java.util.concurrent.TimeUnit;

class HttpClientDefaults {

  static final int CONNECTIONS_MAX_TOTAL = 100;
  static final int CONNECTIONS_MAX_PER_ROUTE = 20;
  static final int CONNECT_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(30);
  static final int READ_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(30);

  private HttpClientDefaults() {}
}
