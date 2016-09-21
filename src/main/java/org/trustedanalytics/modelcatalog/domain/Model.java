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
package org.trustedanalytics.modelcatalog.domain;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import lombok.experimental.Builder;

@Getter
@Builder
public class Model {

  private final UUID id;
  private final String name;
  private final String revision;
  private final String algorithm;
  private final String creationTool;
  private final String description;
  private final String addedBy;
  private final Instant addedOn;
  private final String modifiedBy;
  private final Instant modifiedOn;
  private final Set<UUID> artifactsIds;

}