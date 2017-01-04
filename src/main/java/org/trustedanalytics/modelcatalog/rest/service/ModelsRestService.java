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

import static org.trustedanalytics.modelcatalog.rest.service.ModelMapper.toModelDTO;
import static org.trustedanalytics.modelcatalog.rest.service.ParamsMapper.toParameters;

import org.trustedanalytics.modelcatalog.rest.entities.ModelDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ModelModificationParametersDTO;
import org.trustedanalytics.modelcatalog.service.ModelService;
import org.trustedanalytics.modelcatalog.service.ModelServiceException;
import org.trustedanalytics.modelcatalog.service.ModelServiceExceptionCode;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ModelsRestService {

  private final ModelService modelService;

  @Autowired
  public ModelsRestService(ModelService modelService) {
    this.modelService = modelService;
  }

  public Collection<ModelDTO> listModels(String orgId) {
    return modelService.listModels(orgId).stream()
            .map(ModelMapper::toModelDTO)
            .collect(Collectors.toSet());
  }

  public ModelDTO retrieveModel(UUID modelId) {
    return toModelDTO(modelService.retrieveModel(modelId));
  }

  public ModelDTO addModel(ModelModificationParametersDTO paramsDTO, String orgId) {
    checkRequiredFields(paramsDTO);
    return toModelDTO(modelService.addModel(toParameters(paramsDTO), orgId));
  }

  public ModelDTO updateModel(UUID modelId, ModelModificationParametersDTO paramsDTO) {
    checkRequiredFields(paramsDTO);
    return toModelDTO(modelService.updateModel(modelId, toParameters(paramsDTO)));
  }

  public ModelDTO patchModel(UUID modelId, ModelModificationParametersDTO paramsDTO) {
    return toModelDTO(modelService.patchModel(modelId, toParameters(paramsDTO)));
  }

  public ModelDTO deleteModel(UUID modelId) {
    return toModelDTO(modelService.deleteModel(modelId));
  }

  private void checkRequiredFields(ModelModificationParametersDTO paramsDTO) {
    if (Strings.isNullOrEmpty(paramsDTO.getName())) {
      throw new ModelServiceException(ModelServiceExceptionCode.REQUIRED_FIELDS_MISSING,
              "Non-empty value is required for model name field");
    }
    if (Strings.isNullOrEmpty(paramsDTO.getCreationTool())) {
      throw new ModelServiceException(ModelServiceExceptionCode.REQUIRED_FIELDS_MISSING,
              "Non-empty value is required for model creationTool field");
    }
  }

}
