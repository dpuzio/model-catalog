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
package org.trustedanalytics.modelcatalog.service;

import org.trustedanalytics.modelcatalog.domain.Model;
import org.trustedanalytics.modelcatalog.security.UsernameExtractor;
import org.trustedanalytics.modelcatalog.storage.ModelStore;
import org.trustedanalytics.modelcatalog.storage.ModelStoreException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class ModelService {

  private enum UpdateMode {
    PATCH,
    OVERWRITE,
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(ModelService.class);

  private final ModelStore modelStore;
  private final UsernameExtractor usernameExtractor;

  @Autowired
  public ModelService(ModelStore modelStore, UsernameExtractor usernameExtractor) {
    this.modelStore = modelStore;
    this.usernameExtractor = usernameExtractor;
  }

  public Collection<Model> listModels(UUID orgId) {
    try {
      return modelStore.listModels(orgId);
    } catch (ModelStoreException e) {
      throw new ModelServiceException(
              ModelServiceExceptionCode.MODEL_LIST_FAILED, "Model list failed.", e);
    }
  }

  public Model retrieveModel(UUID modelId) {
    try {
      Model model = modelStore.retrieveModel(modelId);
      if (Objects.isNull(model)) {
        throw new ModelServiceException(
                ModelServiceExceptionCode.MODEL_NOT_FOUND, "Model with given ID not found.");
      }
      return model;
    } catch (ModelStoreException e) {
      throw new ModelServiceException(
              ModelServiceExceptionCode.MODEL_RETRIEVE_FAILED, "Model retrieve failed.", e);
    }
  }

  public Model addModel(ModelModificationParameters params, UUID orgId) {
    try {
      Model model = initiateNewModel(params);
      modelStore.addModel(model, orgId);
      return model;
    } catch (ModelStoreException e) {
      throw new ModelServiceException(
              ModelServiceExceptionCode.MODEL_ADD_FAILED, "Model add failed.", e);
    }
  }

  public Model updateModel(UUID modelId, ModelModificationParameters params) {
    return update(modelId, params, UpdateMode.OVERWRITE);
  }

  public Model patchModel(UUID modelId, ModelModificationParameters params) {
    return update(modelId, params, UpdateMode.PATCH);
  }

  public Model deleteModel(UUID modelId) {
    //TODO delete artifact files: DPNG-10563
    try {
      Model model = retrieveModel(modelId);
      modelStore.deleteModel(modelId);
      return model;
    } catch (ModelStoreException e) {
      throw new ModelServiceException(
              ModelServiceExceptionCode.MODEL_DELETE_FAILED, "Model delete failed.", e);
    }
  }

  private Model initiateNewModel(ModelModificationParameters params) {
    String user = obtainUserName();
    return Model.builder()
            .addedBy(user)
            .addedOn(Instant.now())
            .algorithm(params.getAlgorithm())
            .creationTool(params.getCreationTool())
            .description(params.getDescription())
            .id(UUID.randomUUID())
            .modifiedBy(user)
            .modifiedOn(Instant.now())
            .name(params.getName())
            .revision(params.getRevision())
            .build();
  }

  private String obtainUserName() {
    return usernameExtractor.obtainUsername();
  }

  private Model update(UUID modelId, ModelModificationParameters params, UpdateMode updateMode) {
    try {
      retrieveModel(modelId);
      Map<String, Object> propertiesToUpdate;
      try {
        propertiesToUpdate = PropertiesReader.preparePropertiesToUpdateMap(
                params, updateMode != UpdateMode.OVERWRITE);
      } catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
        throw new ModelServiceException(
                ModelServiceExceptionCode.CANNOT_MAP_PROPERTIES,
                "Model update failed (cannot map properties).",
                e);
      }
      if (propertiesToUpdate.isEmpty()) {
        throw new ModelServiceException(
                ModelServiceExceptionCode.MODEL_NOTHING_TO_UPDATE,
                "Model update failed (nothing to update).");
      }

      addModifiedOnAndByProperties(propertiesToUpdate);
      modelStore.updateModel(modelId, propertiesToUpdate);
      return retrieveModel(modelId);
    } catch (ModelStoreException e) {
      throw new ModelServiceException(
              ModelServiceExceptionCode.MODEL_UPDATE_FAILED, "Model update failed.", e);
    }
  }

  private void addModifiedOnAndByProperties(Map<String, Object> propertiesToUpdate) {
    propertiesToUpdate.put("modifiedBy", obtainUserName());
    propertiesToUpdate.put("modifiedOn", Instant.now());
  }
}
