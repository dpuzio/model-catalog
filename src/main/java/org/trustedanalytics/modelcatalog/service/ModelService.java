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
import org.trustedanalytics.modelcatalog.storage.OperationStatus;

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
    return modelStore.listModels(orgId);
  }

  public Model retrieveModel(UUID modelId) {
    Model model = modelStore.retrieveModel(modelId);
    throwExceptionIfNotFound(model);
    return model; //TODO retrieve artifacts -> DPNG-10149
  }

  public Model addModel(ModelModificationParameters params, UUID orgId) {
    Model model = initiateNewModel(params);
    OperationStatus additionStatus = modelStore.addModel(model, orgId);
    throwExceptionIfUpdateWasNotSuccessful(additionStatus);
    return model;
  }

  public Model updateModel(UUID modelId, ModelModificationParameters params) {
    return update(modelId, params, UpdateMode.OVERWRITE);
  }

  public Model patchModel(UUID modelId, ModelModificationParameters params) {
    return update(modelId, params, UpdateMode.PATCH);
  }

  public Model deleteModel(UUID modelId) {
    Model model = modelStore.retrieveModel(modelId);
    throwExceptionIfNotFound(model);
    //TODO handle artifacts - check if they exist and delete them also
    OperationStatus deleteStatus = modelStore.deleteModel(modelId);
    throwExceptionIfUpdateWasNotSuccessful(deleteStatus);
    return model;
  }

  private Model initiateNewModel(ModelModificationParameters params) {
    String user = obtainUserName();
    return Model.builder()
            .addedBy(user)
            .addedOn(Instant.now())
            .algorithm(params.getAlgorithm())
//            .artifactsIds(params.getArtifactsIds()) TODO artifacts -> DPNG-10149
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
    //TODO handle artifacts -> DPNG-10149 (check if they exist, add, delete)
    retrieveModel(modelId);
    Map<String, Object> propertiesToUpdate;
    try {
      propertiesToUpdate = PropertiesReader.preparePropertiesToUpdateMap(
              params,
              updateMode != UpdateMode.OVERWRITE);
    } catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
      LOGGER.error("Exception while preparing properties map: " + e);
      throw new CannotMapPropertiesException();
    }
    throwExceptionIfNothingToUpdate(propertiesToUpdate);
    addModifiedOnAndByProperties(propertiesToUpdate);
    OperationStatus operationStatus = modelStore.updateModel(modelId, propertiesToUpdate);
    throwExceptionIfUpdateWasNotSuccessful(operationStatus);
    return retrieveModel(modelId);
  }

  private void addModifiedOnAndByProperties(Map<String, Object> propertiesToUpdate) {
    propertiesToUpdate.put("modifiedBy", obtainUserName());
    propertiesToUpdate.put("modifiedOn", Instant.now());
  }

  private void throwExceptionIfNotFound(Model model) {
    if (Objects.isNull(model)) {
      throw new ModelNotFoundException();
    }
  }

  private void throwExceptionIfUpdateWasNotSuccessful(OperationStatus operationStatus) {
    if (operationStatus == OperationStatus.FAILURE) {
      throw new FailedUpdateException();
    }
  }

  private void throwExceptionIfNothingToUpdate(Map<String, Object> propertiesToUpdate) {
    if (propertiesToUpdate.isEmpty()) {
      throw new NothingToUpdateException();
    }
  }

}
