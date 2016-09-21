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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trustedanalytics.modelcatalog.domain.Model;
import org.trustedanalytics.modelcatalog.rest.entities.ModelDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ModelModificationParametersDTO;
import org.trustedanalytics.modelcatalog.service.ModelModificationParameters;
import org.trustedanalytics.modelcatalog.service.ModelService;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RestService {

  private final ModelService modelService;
  private final ModelMapper modelMapper = new ModelMapper();
  private final ParamsMapper paramsMapper = new ParamsMapper();

  @Autowired
  public RestService(ModelService modelService) {
    this.modelService = modelService;
  }

  public Collection<ModelDTO> listModels(UUID orgId) {
    return modelService.listModels(orgId).stream().map(modelMapper).collect(Collectors.toSet());
  }

  public ModelDTO retrieveModel(UUID modelId) {
    return toModelDTO(modelService.retrieveModel(modelId));
  }

  public ModelDTO addModel(ModelModificationParametersDTO paramsDTO, UUID orgId) {
    return toModelDTO(modelService.addModel(toParams(paramsDTO), orgId));
  }

  public ModelDTO updateModel(UUID modelId, ModelModificationParametersDTO paramsDTO) {
    return toModelDTO(modelService.updateModel(modelId, toParams(paramsDTO)));
  }

  public ModelDTO patchModel(UUID modelId, ModelModificationParametersDTO paramsDTO) {
    return toModelDTO(modelService.patchModel(modelId, toParams(paramsDTO)));
  }

  public ModelDTO deleteModel(UUID modelId) {
    return toModelDTO(modelService.deleteModel(modelId));
  }

  private ModelDTO toModelDTO(Model model) {
    return modelMapper.apply(model);
  }

  private ModelModificationParameters toParams(ModelModificationParametersDTO paramsDTO) {
    return paramsMapper.apply(paramsDTO);
  }

}
