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

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
import org.trustedanalytics.modelcatalog.service.ModelServiceException;

@RunWith(MockitoJUnitRunner.class)
public class ModelsRestServiceTest {

  @Mock
  private ModelService modelService;

  @InjectMocks
  ModelsRestService service;
  
  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  private static final String DEFAULT_ORG_ID = "defaultorg";
  private final UUID modelId = UUID.randomUUID();
  private final Model model = TestModelsBuilder.exemplaryModel();
  private final ModelDTO modelDTO = ModelMapper.toModelDTO(model);
  private final ModelModificationParametersDTO paramsDTO =
      TestModelParamsBuilder.exemplaryParamsDTO();

  @Test
  public void shouldListAndMapModels() {
    // given
    when(modelService.listModels(DEFAULT_ORG_ID)).thenReturn(Collections.singletonList(model));
    // when
    Collection<ModelDTO> modelDTOs = service.listModels(DEFAULT_ORG_ID);
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
    assertThat(retrievedModel).isEqualToComparingFieldByFieldRecursively(modelDTO);
  }

  @Test
  public void shouldAddAndMapModel() {
    // given
    when(modelService.addModel(any(ModelModificationParameters.class), eq(DEFAULT_ORG_ID)))
        .thenReturn(model);
    // when
    ModelDTO addedModel = service.addModel(paramsDTO, DEFAULT_ORG_ID);
    // then
    assertThat(addedModel).isEqualToComparingFieldByFieldRecursively(modelDTO);
  }

  @Test
  public void shouldUpdateAndMapModel() {
    // given
    when(modelService.updateModel(eq(modelId), any(ModelModificationParameters.class)))
        .thenReturn(model);
    // when
    ModelDTO updatedModel = service.updateModel(modelId, paramsDTO);
    // then
    assertThat(updatedModel).isEqualToComparingFieldByFieldRecursively(modelDTO);
  }

  @Test
  public void shouldPatchAndMapModel() {
    // given
    when(modelService.patchModel(eq(modelId), any(ModelModificationParameters.class)))
        .thenReturn(model);
    // when
    ModelDTO patchedModel = service.patchModel(modelId, paramsDTO);
    // then
    assertThat(patchedModel).isEqualToComparingFieldByFieldRecursively(modelDTO);
  }

  @Test
  public void shouldDeleteAndMapModel() {
    // given
    when(modelService.deleteModel(modelId)).thenReturn(model);
    // when
    ModelDTO deletedModel = service.deleteModel(modelId);
    // then
    assertThat(deletedModel).isEqualToComparingFieldByFieldRecursively(modelDTO);
  }

  @Test
  public void shouldThrowAnExceptionWhenGivenInvalidModelName() throws Exception {
    // given
    ModelModificationParametersDTO modParamsDTO = new ModelModificationParametersDTO("\t \n",
        "revision", "algorithm", "creationTool", "description");

    // when
    // then
    thrown.expect(ModelServiceException.class);
    service.addModel(modParamsDTO, DEFAULT_ORG_ID);
  }
  
  @Test
  public void shouldThrowAnExceptionWhenGivenInvalidCreationToolValue() throws Exception {
    // given
    ModelModificationParametersDTO modParamsDTO = new ModelModificationParametersDTO("name",
        "revision", "algorithm", "\r \n", "description");

    // when
    // then
    thrown.expect(ModelServiceException.class);
    service.addModel(modParamsDTO, DEFAULT_ORG_ID);
  }

}
