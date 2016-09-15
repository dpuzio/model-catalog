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
package org.trustedanalytics.modelcatalog;

import org.trustedanalytics.modelcatalog.domain.Model;

import com.google.common.collect.Sets;

import java.time.Instant;
import java.util.HashSet;
import java.util.UUID;

public class TestModelsBuilder {

  public static final UUID ID = UUID.randomUUID();
  public static final String ADDED_BY = "Grzegorz Brzeczyszczykiewicz";
  public static final Instant ADDED_ON = Instant.now();
  public static final String ALGORITHM = "The Algorithm";
  public static final String CREATION_TOOL = "Other";
  public static final HashSet<UUID> ARTIFACTS_IDS = Sets.newHashSet(UUID.randomUUID(), UUID
          .randomUUID());
  public static final String DESCRIPTION = "Description";
  public static final String MODIFIED_BY = "ModifiedBy";
  public static final Instant MODIFIED_ON = Instant.now();
  public static final String NAME = "Model Name";
  public static final String REVISION = "Revision";

  public static Model exemplaryModel() {
    return Model.builder()
            .id(ID)
            .addedBy(ADDED_BY)
            .addedOn(ADDED_ON)
            .algorithm(ALGORITHM)
            .creationTool(CREATION_TOOL)
//            .artifactsIds(ARTIFACTS_IDS) TODO DPNG-10149
            .description(DESCRIPTION)
            .modifiedBy(MODIFIED_BY)
            .modifiedOn(MODIFIED_ON)
            .name(NAME)
            .revision(REVISION)
            .build();
  }

  public static Model emptyModel() {
    return Model.builder().build();
  }

}
