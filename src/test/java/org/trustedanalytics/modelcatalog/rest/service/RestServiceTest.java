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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.trustedanalytics.modelcatalog.TestModelParamsBuilder;
import org.trustedanalytics.modelcatalog.TestModelsBuilder;
import org.trustedanalytics.modelcatalog.domain.Model;
import org.trustedanalytics.modelcatalog.rest.entities.ModelDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ModelModificationParametersDTO;
import org.trustedanalytics.modelcatalog.service.ModelModificationParameters;
import org.trustedanalytics.modelcatalog.service.ModelService;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class RestServiceTest {

  @Mock
  private ModelService modelService;

  @InjectMocks
  RestService service;

  private final UUID orgId = UUID.randomUUID();
  private final UUID modelId = UUID.randomUUID();
  private final Model model = TestModelsBuilder.exemplaryModel();
  private final ModelDTO modelDTO = new ModelMapper().apply(model);
  private final ModelModificationParametersDTO paramsDTO = TestModelParamsBuilder
          .exemplaryParamsDTO();

  @Test
  public void shouldListAndMapModels() {
    // given
    when(modelService.listModels(orgId)).thenReturn(Collections.singletonList(model));
    // when
    Collection<ModelDTO> modelDTOs = service.listModels(orgId);
    // then
    assertThat(modelDTOs).hasSize(1);
    assertThat(modelDTOs.contains(modelDTO));
  }

  @Test
  public void shouldRetrieveAndMapModel() {
    // given
    when(modelService.retrieveModel(modelId)).thenReturn(model);
    // when
    ModelDTO retrievedModel = service.retrieveModel(modelId);
    // then
    assertThat(retrievedModel).isEqualToComparingFieldByField(modelDTO);
  }

  @Test
  public void shouldAddAndMapModel() {
    // given
    when(modelService.addModel(any(ModelModificationParameters.class), eq(orgId))).thenReturn
            (model);
    // when
    ModelDTO addedModel = service.addModel(paramsDTO, orgId);
    // then
    assertThat(addedModel).isEqualToComparingFieldByField(modelDTO);
  }

  @Test
  public void shouldUpdateAndMapModel() {
    // given
    when(modelService.updateModel(eq(modelId), any(ModelModificationParameters.class)))
            .thenReturn(model);
    // when
    ModelDTO updatedModel = service.updateModel(modelId, paramsDTO);
    // then
    assertThat(updatedModel).isEqualToComparingFieldByField(modelDTO);
  }

  @Test
  public void shouldPatchAndMapModel() {
    // given
    when(modelService.patchModel(eq(modelId), any(ModelModificationParameters.class))).thenReturn
            (model);
    // when
    ModelDTO patchedModel = service.patchModel(modelId, paramsDTO);
    // then
    assertThat(patchedModel).isEqualToComparingFieldByField(modelDTO);
  }

  @Test
  public void shouldDeleteAndMapModel() {
    // given
    when(modelService.deleteModel(modelId)).thenReturn(model);
    // when
    ModelDTO deletedModel = service.deleteModel(modelId);
    // then
    assertThat(deletedModel).isEqualToComparingFieldByField(modelDTO);
  }

}