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

package org.trustedanalytics.modelcatalog.healthcheck;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class HealthCheckTestObject {

  private final long timestamp;
  private final String id;

  public HealthCheckTestObject() {
    Instant instant = Instant.now();
    this.timestamp = instant.getEpochSecond();
    this.id = UUID.randomUUID().toString();
  }
}