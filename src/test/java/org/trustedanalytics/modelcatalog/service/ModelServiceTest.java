/**
 * Copyright (c) 2016 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trustedanalytics.modelcatalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Sets;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.trustedanalytics.modelcatalog.ModelParamsChecker;
import org.trustedanalytics.modelcatalog.TestModelParamsBuilder;
import org.trustedanalytics.modelcatalog.TestModelsBuilder;
import org.trustedanalytics.modelcatalog.domain.Model;
import org.trustedanalytics.modelcatalog.security.UsernameExtractor;
import org.trustedanalytics.modelcatalog.storage.ModelStore;
import org.trustedanalytics.modelcatalog.storage.OperationStatus;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class ModelServiceTest {

  @Mock
  private ModelStore modelStore;
  @Mock
  private UsernameExtractor usernameExtractor;
  @InjectMocks
  private ModelService modelService;

  @Captor
  private ArgumentCaptor<Map<String, Object>> propertiesToUpdateMapCaptor;

  private static final String MODIFIED_BY_PROPERTY_NAME = "modifiedBy";
  private static final String MODIFIED_ON_PROPERTY_NAME = "modifiedOn";
  private static final String NAME_PROPERTY_NAME = "name";
  private static final String USERNAME = "username";

  private final Model model = TestModelsBuilder.exemplaryModel();
  private final UUID modelId = TestModelsBuilder.ID;
  private final ModelModificationParameters params = TestModelParamsBuilder.exemplaryParams();

  @Before
  public void setUp() {
    when(usernameExtractor.obtainUsername()).thenReturn(USERNAME);
  }

  @Test
  public void shouldListModels() {
    // given
    Set<Model> models = Sets.newHashSet(TestModelsBuilder.exemplaryModel(), TestModelsBuilder.exemplaryModel());
    when(modelStore.listModels(any(UUID.class))).thenReturn(models);
    // when
    Collection<Model> returnedModels = modelService.listModels(UUID.randomUUID());
    // then
    assertThat(models).isEqualTo(returnedModels);
  }

  @Test
  public void shouldRetrieveExistingModel() {
    // given
    when(modelStore.retrieveModel(modelId)).thenReturn(model);
    // when
    Model retrievedModel = modelService.retrieveModel(modelId);
    // then
    assertThat(retrievedModel).isEqualToComparingFieldByField(model);
  }

  @Test(expected = ModelNotFoundException.class)
  public void retrieveModel_shouldThrowException_whenNoModelFound() {
    when(modelStore.retrieveModel(any(UUID.class))).thenReturn(null);
    modelService.retrieveModel(UUID.randomUUID());
  }

  @Test
  public void shouldInitiateAddAndReturnModel_withGivenProperties() {
    // given
    when(modelStore.addModel(any(Model.class), any(UUID.class))).thenReturn(OperationStatus.SUCCESS);
    // when
    Instant before = Instant.now();
    Model addedModel = modelService.addModel(params, UUID.randomUUID());
    Instant after = Instant.now();
    // then
    ModelParamsChecker.checkThatModelDTOContainsParamsDTO(addedModel, params);
    assertThat(addedModel.getId()).isNotNull();
    assertThat(addedModel.getAddedBy()).isEqualTo(USERNAME);
    checkThatIsBetween(addedModel.getAddedOn(), before, after);
    assertThat(addedModel.getModifiedBy()).isEqualTo(USERNAME);
    checkThatIsBetween(addedModel.getModifiedOn(), before, after);
  }

  @Test(expected = FailedUpdateException.class)
  public void addModel_shouldThrowFailedUpdateException_whenStatusFailure() {
    // given
    when(modelStore.addModel(any(Model.class), any(UUID.class))).thenReturn(OperationStatus.FAILURE);
    // when
    modelService.addModel(params, UUID.randomUUID());
  }

  @Test
  public void shouldUpdateAndReturnRetrievedModel() {
    // given
    when(modelStore.retrieveModel(modelId)).thenReturn(model);
    when(modelStore.updateModel(eq(modelId), any(Map.class))).thenReturn(OperationStatus.SUCCESS);
    // when
    Model updatedModel = modelService.updateModel(modelId, params);
    // then
    assertThat(updatedModel).isSameAs(model);
  }

  @Test(expected = ModelNotFoundException.class)
  public void updateModel_shouldThrowException_whenModelNotFound() {
    // given
    when(modelStore.retrieveModel(modelId)).thenReturn(null);
    // when
    modelService.updateModel(modelId, params);
  }

  @Test
  public void updateModel_shouldPassPropertiesMapContainingNullProperties() {
    // given
    when(modelStore.retrieveModel(modelId)).thenReturn(model);
    ModelModificationParameters params = TestModelParamsBuilder.paramsWithNullNameProperty();
    // when
    modelService.updateModel(modelId, params);
    // then
    verify(modelStore).updateModel(eq(modelId), propertiesToUpdateMapCaptor.capture());
    Map<String, Object> propertiesMap = propertiesToUpdateMapCaptor.getValue();
    assertThat(propertiesMap).containsKey(NAME_PROPERTY_NAME);
    assertThat(propertiesMap.get(NAME_PROPERTY_NAME)).isNull();
  }

  @Test
  public void updateModel_shouldUpdateModifiedOnAndByProperties() {
    // given
    when(modelStore.retrieveModel(modelId)).thenReturn(model);
    // when
    Instant before = Instant.now();
    modelService.updateModel(modelId, params);
    Instant after = Instant.now();
    // then
    verify(modelStore).updateModel(eq(modelId), propertiesToUpdateMapCaptor.capture());
    Map<String, Object> propertiesMap = propertiesToUpdateMapCaptor.getValue();
    assertThat(propertiesMap).containsKey(MODIFIED_BY_PROPERTY_NAME);
    assertThat(propertiesMap.get(MODIFIED_BY_PROPERTY_NAME)).isEqualTo(USERNAME);
    assertThat(propertiesMap).containsKey(MODIFIED_ON_PROPERTY_NAME);
    checkThatIsBetween((Instant) propertiesMap.get(MODIFIED_ON_PROPERTY_NAME), before, after);
  }

  @Test(expected = FailedUpdateException.class)
  public void updateModel_shouldThrowFailedUpdateException_whenUpdateWasNotSuccessful() {
    // given
    when(modelStore.retrieveModel(any(UUID.class))).thenReturn(model);
    when(modelStore.updateModel(any(UUID.class), any(Map.class))).thenReturn(OperationStatus.FAILURE);
    // when
    modelService.updateModel(UUID.randomUUID(), params);
  }

  @Test
  public void shouldPatchAndReturnRetrievedModel() {
    // given
    when(modelStore.retrieveModel(modelId)).thenReturn(model);
    when(modelStore.updateModel(eq(modelId), any(Map.class))).thenReturn(OperationStatus.SUCCESS);
    // when
    Model patchedModel = modelService.patchModel(modelId, params);
    // then
    assertThat(patchedModel).isSameAs(model);
  }

  @Test(expected = ModelNotFoundException.class)
  public void patchModel_shouldThrowException_whenModelNotFound() {
    // given
    when(modelStore.retrieveModel(modelId)).thenReturn(null);
    // when
    modelService.patchModel(modelId, params);
  }

  @Test
  public void patchModel_shouldPassPropertiesMapOmittingNullProperties() {
    // given
    when(modelStore.retrieveModel(modelId)).thenReturn(model);
    ModelModificationParameters params = TestModelParamsBuilder.paramsWithNullNameProperty();
    // when
    modelService.patchModel(modelId, params);
    // then
    verify(modelStore).updateModel(eq(modelId), propertiesToUpdateMapCaptor.capture());
    Map<String, Object> propertiesMap = propertiesToUpdateMapCaptor.getValue();
    assertThat(propertiesMap).doesNotContainKey(NAME_PROPERTY_NAME);
  }

  @Test
  public void patchModel_shouldUpdateModifiedOnAndByProperties() {
    // given
    when(modelStore.retrieveModel(modelId)).thenReturn(model);
    // when
    Instant before = Instant.now();
    modelService.patchModel(modelId, params);
    Instant after = Instant.now();
    // then
    verify(modelStore).updateModel(eq(modelId), propertiesToUpdateMapCaptor.capture());
    Map<String, Object> propertiesMap = propertiesToUpdateMapCaptor.getValue();
    assertThat(propertiesMap).containsKey(MODIFIED_BY_PROPERTY_NAME);
    assertThat(propertiesMap.get(MODIFIED_BY_PROPERTY_NAME)).isEqualTo(USERNAME);
    assertThat(propertiesMap).containsKey(MODIFIED_ON_PROPERTY_NAME);
    checkThatIsBetween((Instant) propertiesMap.get(MODIFIED_ON_PROPERTY_NAME), before, after);
  }

  @Test(expected = FailedUpdateException.class)
  public void patchModel_shouldThrowFailedUpdateException_whenUpdateWasNotSuccessful() {
    // given
    when(modelStore.retrieveModel(any(UUID.class))).thenReturn(model);
    when(modelStore.updateModel(any(UUID.class), any(Map.class))).thenReturn(OperationStatus.FAILURE);
    // when
    modelService.patchModel(UUID.randomUUID(), params);
  }

  @Test(expected = NothingToUpdateException.class)
  public void patchModel_shouldThrowExceptionIfNothingToUpdate() {
    // given
    when(modelStore.retrieveModel(any(UUID.class))).thenReturn(model);
    // when
    modelService.patchModel(UUID.randomUUID(), TestModelParamsBuilder.emptyParams());
  }

  @Test
  public void shouldDeleteAndReturnModel() {
    // given
    when(modelStore.retrieveModel(modelId)).thenReturn(model);
    // when
    Model deletedModel = modelService.deleteModel(modelId);
    // then
    assertThat(deletedModel).isSameAs(model);
    verify(modelStore).deleteModel(modelId);
  }

  @Test(expected = ModelNotFoundException.class)
  public void testDeleteModel_shouldThrowException_whenModelNotFound() {
    // given
    when(modelStore.retrieveModel(modelId)).thenReturn(null);
    // when
    modelService.deleteModel(modelId);
  }

  @Test(expected = FailedUpdateException.class)
  public void deleteModel_shouldThrowFailedUpdateException_whenUpdateWasNotSuccessful() {
    // given
    when(modelStore.retrieveModel(any(UUID.class))).thenReturn(model);
    when(modelStore.deleteModel(any(UUID.class))).thenReturn(OperationStatus.FAILURE);
    // when
    modelService.deleteModel(UUID.randomUUID());
  }

  private void checkThatIsBetween(Instant instant, Instant start, Instant end) {
    assertThat(instant).isGreaterThanOrEqualTo(start);
    assertThat(instant).isLessThanOrEqualTo(end);
  }

}