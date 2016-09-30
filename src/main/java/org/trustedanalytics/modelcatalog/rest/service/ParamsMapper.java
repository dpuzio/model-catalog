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
package org.trustedanalytics.modelcatalog.rest.service;

import org.trustedanalytics.modelcatalog.rest.entities.ModelModificationParametersDTO;
import org.trustedanalytics.modelcatalog.service.ModelModificationParameters;

class ParamsMapper {

  private ParamsMapper() {
  }

  static ModelModificationParameters toParameters(
          ModelModificationParametersDTO modelModParamsDTO) {
    return ModelModificationParameters.builder()
            .algorithm(modelModParamsDTO.getAlgorithm())
            .creationTool(modelModParamsDTO.getCreationTool())
            .description(modelModParamsDTO.getDescription())
            .name(modelModParamsDTO.getName())
            .revision(modelModParamsDTO.getRevision())
            .build();
  }

}
