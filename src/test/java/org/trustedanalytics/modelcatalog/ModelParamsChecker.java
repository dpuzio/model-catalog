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

import static org.assertj.core.api.Assertions.assertThat;

import org.trustedanalytics.modelcatalog.domain.Model;
import org.trustedanalytics.modelcatalog.rest.entities.ModelDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ModelModificationParametersDTO;
import org.trustedanalytics.modelcatalog.service.ModelModificationParameters;

public class ModelParamsChecker {

  public static void checkThatModelDTOContainsParamsDTO(Model model, ModelModificationParameters
          params) {
    assertThat(model.getAlgorithm()).isEqualTo(params.getAlgorithm());
    assertThat(model.getArtifactsIds()).isEqualTo(params.getArtifactsIds());
    assertThat(model.getCreationTool()).isEqualTo(params.getCreationTool());
    assertThat(model.getDescription()).isEqualTo(params.getDescription());
    assertThat(model.getName()).isEqualTo(params.getName());
    assertThat(model.getRevision()).isEqualTo(params.getRevision());
  }

  public static void checkThatModelDTOContainsParamsDTO(ModelDTO model,
                                                        ModelModificationParametersDTO params) {
    assertThat(model.getAlgorithm()).isEqualTo(params.getAlgorithm());
    assertThat(model.getArtifactsIds()).isEqualTo(params.getArtifactsIds());
    assertThat(model.getCreationTool()).isEqualTo(params.getCreationTool());
    assertThat(model.getDescription()).isEqualTo(params.getDescription());
    assertThat(model.getName()).isEqualTo(params.getName());
    assertThat(model.getRevision()).isEqualTo(params.getRevision());
  }

}
