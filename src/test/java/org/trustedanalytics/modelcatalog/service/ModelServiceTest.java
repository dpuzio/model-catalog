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
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Sets;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.trustedanalytics.modelcatalog.TestModelsBuilder;
import org.trustedanalytics.modelcatalog.domain.Model;
import org.trustedanalytics.modelcatalog.storage.ModelStore;
import org.trustedanalytics.modelcatalog.storage.OperationStatus;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class ModelServiceTest {

  @Mock
  private ModelStore modelStore;

  @InjectMocks
  private ModelService modelService;

  @Captor
  private ArgumentCaptor<Map<String, Object>> propertiesToUpdateMapCaptor;

  @Test
  public void shouldListModels() {
    // given
    Set<Model> models = Sets.newHashSet(TestModelsBuilder.prepareExemplaryModel(), TestModelsBuilder.prepareExemplaryModel());
    when(modelStore.listModels(any(UUID.class))).thenReturn(models);
    // when
    Collection<Model> returnedModels = modelService.listModels(UUID.randomUUID());
    // then
    assertThat(models).isEqualTo(returnedModels);
  }

  @Test
  public void shouldRetrieveExistingModel() {
    // given
    UUID modelId = UUID.randomUUID();
    Model model = TestModelsBuilder.prepareExemplaryModel(modelId);
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
  public void shouldAddAndReturnModel_withGeneratedUUIDAndGivenProperties() {
    // given
    UUID modelId = UUID.randomUUID();
    Model model = TestModelsBuilder.prepareExemplaryModel(modelId);
    when(modelStore.addModel(same(model), any(UUID.class))).thenReturn(OperationStatus.SUCCESS);
    // when
    Model addedModel = modelService.addModel(model, UUID.randomUUID());
    // then
    assertThat(addedModel.getId()).isNotEqualTo(modelId);
    model.setId(addedModel.getId());
    assertThat(addedModel).isEqualToComparingFieldByField(model);
  }

  @Test(expected = FailedUpdateException.class)
  public void addModel_shouldThrowFailedUpdateException_whenStatusFailure() {
    // given
    when(modelStore.addModel(any(Model.class), any(UUID.class))).thenReturn(OperationStatus.FAILURE);
    // when
    modelService.addModel(new Model(), UUID.randomUUID());
  }

  @Test(expected = MismatchedIdsException.class)
  public void updateModel_shouldThrowException_whenIdInModelEntityIsGivenAndIsDifferentThanExplicitlyGivenId() {
    // when
    modelService.updateModel(UUID.randomUUID(), TestModelsBuilder.prepareExemplaryModel());
  }

  @Test(expected = MismatchedIdsException.class)
  public void patchModel_shouldThrowException_whenIdInModelEntityIsGivenAndIsDifferentThanExplicitlyGivenId() {
    // when
    modelService.patchModel(UUID.randomUUID(), TestModelsBuilder.prepareExemplaryModel());
  }

  @Test
  public void updateModel_shouldUpdateAndReturnModelWithGivenId() {
    // given
    final UUID id = UUID.randomUUID();
    when(modelStore.retrieveModel(id)).thenReturn(new Model());
    // when
    Model returnedModel = modelService.updateModel(id, new Model());
    // then
    assertThat(returnedModel.getId()).isEqualTo(id);
  }

  @Test
  public void patchModel_shouldUpdateAndReturnModelRetrievedFromDbWithGivenId() {
    // given
    final UUID id = UUID.randomUUID();
    Model model = TestModelsBuilder.prepareExemplaryModel(id);
    Model modelRetrievedFromDb = new Model();
    when(modelStore.retrieveModel(id)).thenReturn(modelRetrievedFromDb);
    // when
    Model returnedModel = modelService.patchModel(id, model);
    // then
    assertThat(returnedModel).isSameAs(modelRetrievedFromDb);
  }

  @Test
  public void updateModel_shouldPreparePropertiesMapContainingNullProperties() {
    // given
    final UUID modelId = UUID.randomUUID();
    when(modelStore.retrieveModel(modelId)).thenReturn(new Model());
    Model model = TestModelsBuilder.prepareExemplaryModel(modelId);
    final String nullProperty = "name";
    model.setName(null);
    // when
    modelService.updateModel(modelId, model);
    // then
    verify(modelStore).updateModel(eq(modelId), propertiesToUpdateMapCaptor.capture());
    Map<String, Object> propertiesMap = propertiesToUpdateMapCaptor.getValue();
    assertThat(propertiesMap).containsKey(nullProperty);
    assertThat(propertiesMap.get(nullProperty)).isNull();
  }

  @Test
  public void patchModel_shouldPreparePropertiesMapOmittingNullProperties() {
    // given
    UUID modelId = UUID.randomUUID();
    Model model = TestModelsBuilder.prepareExemplaryModel(modelId);
    final String nullProperty = "name";
    model.setName(null);
    when(modelStore.retrieveModel(modelId)).thenReturn(new Model());
    // when
    modelService.patchModel(modelId, model);
    // then
    verify(modelStore).updateModel(eq(modelId), propertiesToUpdateMapCaptor.capture());
    Map<String, Object> propertiesMap = propertiesToUpdateMapCaptor.getValue();
    assertThat(propertiesMap).doesNotContainKey(nullProperty);
  }

  @Test(expected = NothingToUpdateException.class)
  public void patchModel_shouldThrowExceptionIfNothingToUpdate() {
    // given
    when(modelStore.retrieveModel(any(UUID.class))).thenReturn(new Model());
    // when
    modelService.patchModel(UUID.randomUUID(), new Model());
  }

  @Test(expected = FailedUpdateException.class)
  public void updateModel_shouldThrowFailedUpdateException_whenUpdateWasNotSuccessful() {
    // given
    when(modelStore.retrieveModel(any(UUID.class))).thenReturn(new Model());
    when(modelStore.updateModel(any(UUID.class), any(Map.class))).thenReturn(OperationStatus.FAILURE);
    // when
    modelService.updateModel(UUID.randomUUID(), new Model());
  }

  @Test(expected = FailedUpdateException.class)
  public void patchModel_shouldThrowFailedUpdateException_whenUpdateWasNotSuccessful() {
    // given
    UUID modelId = UUID.randomUUID();
    Model model = TestModelsBuilder.prepareExemplaryModel(modelId);
    when(modelStore.retrieveModel(modelId)).thenReturn(new Model());
    when(modelStore.updateModel(any(UUID.class), any(Map.class))).thenReturn(OperationStatus.FAILURE);
    // when
    modelService.patchModel(modelId, model);
  }

  @Test
  public void shouldDeleteAndReturnModel() {
    // given
    final UUID modelId = UUID.randomUUID();
    final Model model = new Model();
    when(modelStore.retrieveModel(modelId)).thenReturn(model);
    // when
    Model deletedModel = modelService.deleteModel(modelId);
    // then
    assertThat(deletedModel).isSameAs(model);
    verify(modelStore).deleteModel(modelId);
  }

  @Test(expected = ModelNotFoundException.class)
  public void testDeleteModel_shouldThrowException_whenModelNotFound() throws Exception {
    // given
    UUID modelId = UUID.randomUUID();
    when(modelStore.retrieveModel(modelId)).thenReturn(null);
    // when
    modelService.deleteModel(modelId);
  }

  @Test(expected = FailedUpdateException.class)
  public void deleteModel_shouldThrowFailedUpdateException_whenUpdateWasNotSuccessful() {
    // given
    when(modelStore.retrieveModel(any(UUID.class))).thenReturn(new Model());
    when(modelStore.deleteModel(any(UUID.class))).thenReturn(OperationStatus.FAILURE);
    // when
    modelService.deleteModel(UUID.randomUUID());
  }

}