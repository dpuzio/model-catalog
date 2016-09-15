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

import org.trustedanalytics.modelcatalog.domain.Model;
import org.trustedanalytics.modelcatalog.rest.entities.ModelDTO;

import java.time.Instant;
import java.util.function.Function;

class ModelMapper implements Function<Model, ModelDTO> {

  @Override
  public ModelDTO apply(Model model) {
    return ModelDTO.builder()
            .addedBy(model.getAddedBy())
            .addedOn(format(model.getAddedOn()))
            .algorithm(model.getAlgorithm())
//              .artifactsIds(model.getArtifactsIds()) TODO mapArtifacts -> DPNG-10149
            .creationTool(model.getCreationTool())
            .description(model.getDescription())
            .id(model.getId())
            .modifiedBy(model.getModifiedBy())
            .modifiedOn(format(model.getModifiedOn()))
            .name(model.getName())
            .revision(model.getRevision())
            .build();
  }

  private String format(Instant instant) {
    return InstantFormatter.format(instant);
  }

}
