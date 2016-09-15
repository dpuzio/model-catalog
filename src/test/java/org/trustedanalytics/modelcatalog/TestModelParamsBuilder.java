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

import org.trustedanalytics.modelcatalog.rest.entities.ModelModificationParametersDTO;
import org.trustedanalytics.modelcatalog.service.ModelModificationParameters;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.UUID;

public class TestModelParamsBuilder {

  public static final String ALGORITHM = "New Algorithm";
  public static final String CREATION_TOOL = "New creation tool";
  public static final HashSet<UUID> ARTIFACTS_IDS = Sets.newHashSet(UUID.randomUUID());
  public static final String DESCRIPTION = "New Description";
  public static final String NAME = "New Name";
  public static final String REVISION = "New Revision";

  public static ModelModificationParameters exemplaryParams() {
    return ModelModificationParameters.builder()
            .algorithm(ALGORITHM)
            .artifactsIds(ARTIFACTS_IDS)
            .creationTool(CREATION_TOOL)
            .description(DESCRIPTION)
            .name(NAME)
            .revision(REVISION)
            .build();
  }

  public static ModelModificationParametersDTO exemplaryParamsDTO() {
    return ModelModificationParametersDTO.builder()
            .algorithm(ALGORITHM)
            .artifactsIds(ARTIFACTS_IDS)
            .creationTool(CREATION_TOOL)
            .description(DESCRIPTION)
            .name(NAME)
            .revision(REVISION)
            .build();
  }

  public static ModelModificationParameters paramsWithNullNameProperty() {
    return ModelModificationParameters.builder()
            .algorithm(ALGORITHM)
            .artifactsIds(ARTIFACTS_IDS)
            .creationTool(CREATION_TOOL)
            .description(DESCRIPTION)
            .name(null)
            .revision(REVISION)
            .build();
  }

  public static ModelModificationParameters emptyParams() {
    return ModelModificationParameters.builder().build();
  }

  public static ModelModificationParametersDTO emptyParamsDTO() {
    return ModelModificationParametersDTO.builder().build();
  }

}
