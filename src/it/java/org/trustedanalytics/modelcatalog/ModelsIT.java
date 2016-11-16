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
import static org.trustedanalytics.modelcatalog.ExpectedExceptionsHelper.expectHttpClientErrorException;
import static org.trustedanalytics.modelcatalog.ExpectedExceptionsHelper.expectModelCatalogExceptionWithStatus;
import static org.trustedanalytics.modelcatalog.ExpectedExceptionsHelper.expectModelCatalogExceptionWithStatusAndReason;

import org.trustedanalytics.modelcatalog.rest.ModelCatalogPaths;
import org.trustedanalytics.modelcatalog.rest.client.ModelCatalogClientBuilder;
import org.trustedanalytics.modelcatalog.rest.client.ModelCatalogReaderClient;
import org.trustedanalytics.modelcatalog.rest.client.ModelCatalogWriterClient;
import org.trustedanalytics.modelcatalog.rest.entities.ModelDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ModelModificationParametersDTO;
import org.trustedanalytics.modelcatalog.rest.service.InstantFormatter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import javax.annotation.PostConstruct;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, FongoConfig.class})
@WebAppConfiguration
@IntegrationTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles({"integration-test", "in-memory"})
public class ModelsIT {

  @Value("http://localhost:${local.server.port}")
  private String url;

  private ModelCatalogReaderClient modelCatalogReader;
  private ModelCatalogWriterClient modelCatalogWriter;

  private final UUID ORG_ID = UUID.randomUUID();
  private final ModelModificationParametersDTO PARAMS = TestModelParamsBuilder.exemplaryParamsDTO();
  private ModelDTO addedModel;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @PostConstruct
  public void init() {
    modelCatalogReader = new ModelCatalogClientBuilder(url).buildReader();
    modelCatalogWriter = new ModelCatalogClientBuilder(url).buildWriter();
  }

  @Test
  public void client_shouldListAddRetrieveAndDeleteModels() {
    checkThatThereAreNoModelsInDb();
    addModelAndCheckThatItWasProperlyInitialized();
    checkThatThereIsOneModelInDb();
    retrieveModelFromDbAndCompareWithTheAddedOne();
    deleteAddedModel();
    checkThatThereAreNoModelsInDb();
  }

  @Test
  public void addModel_shouldReturn400_whenRequiredFieldsNotProvided() {
    expectModelCatalogExceptionWithStatusAndReason(thrown, HttpStatus.BAD_REQUEST);
    modelCatalogWriter.addModel(new ModelModificationParametersDTO(), UUID.randomUUID());
  }

  @Test
  public void retrieveModel_shouldReturn404_whenModelNotFound() {
    expectModelCatalogExceptionWithStatusAndReason(thrown, HttpStatus.NOT_FOUND);
    modelCatalogReader.retrieveModel(UUID.randomUUID());
  }

  @Test
  public void retrieveModel_shouldReturn400_whenAnyParameterIsInstanceOfWrongType() {
    RestTemplate restTemplate = new RestTemplate();
    String tryToGetModelWhenOrgIdParamIsNotUuidTypeUrl =
            this.url + ModelCatalogPaths.MODELS + "/string-instead-of-uuid";
    expectHttpClientErrorException(thrown, HttpStatus.BAD_REQUEST);
    ResponseEntity<ModelDTO> response = restTemplate.getForEntity
            (tryToGetModelWhenOrgIdParamIsNotUuidTypeUrl, ModelDTO.class);
  }

  @Test
  public void retrieveModelsList_shouldReturn400_whenOrgIdParameterNotSet() {
    RestTemplate restTemplate = new RestTemplate();
    String tryToGetModelsWithoutOrgIdParamUrl = this.url + ModelCatalogPaths.MODELS;
    expectHttpClientErrorException(thrown, HttpStatus.BAD_REQUEST);
    ResponseEntity<ModelDTO> response = restTemplate.getForEntity
            (tryToGetModelsWithoutOrgIdParamUrl, ModelDTO.class);
  }

  @Test
  public void retrieveModelsList_shouldReturn404_whenInvokeNotImplementedMethod() {
    RestTemplate restTemplate = new RestTemplate();
    String tryToDeleteModelsOnGetModelsUrl = this.url + ModelCatalogPaths.MODELS;
    expectHttpClientErrorException(thrown, HttpStatus.NOT_FOUND);
    restTemplate.delete(tryToDeleteModelsOnGetModelsUrl);
  }

  @Test
  public void updateModel_shouldUpdateAllFields() {
    // given
    addExemplaryModel();
    final UUID modelId = addedModel.getId();
    // when
    Instant before = currentTimeWithPrecisionToSeconds();
    ModelDTO updatedModel = modelCatalogWriter.updateModel(modelId, PARAMS);
    Instant after = Instant.now();
    // then
    ModelParamsChecker.checkThatModelDTOContainsParamsDTO(updatedModel, PARAMS);
    assertThat(updatedModel.getId()).isEqualTo(modelId);
    assertThat(updatedModel.getAddedBy()).isEqualTo(addedModel.getAddedBy());
    assertThat(updatedModel.getAddedOn()).isEqualTo(addedModel.getAddedOn());
    assertThat(updatedModel.getModifiedBy()).isEqualTo(ITSecurityConfig.USERNAME);
    checkThatIsBetween(updatedModel.getModifiedOn(), before, after);
  }

  @Test
  public void updateModel_shouldReturn404_whenModelNotFound() {
    expectModelCatalogExceptionWithStatusAndReason(thrown, HttpStatus.NOT_FOUND);
    modelCatalogWriter.updateModel(UUID.randomUUID(), PARAMS);
  }

  @Test
  public void updateModel_shouldReturn400_whenRequiredFieldsNotProvided() {
    addExemplaryModel();
    final UUID modelId = addedModel.getId();
    expectModelCatalogExceptionWithStatusAndReason(thrown, HttpStatus.BAD_REQUEST);
    modelCatalogWriter.updateModel(modelId, TestModelParamsBuilder.emptyParamsDTO());
  }

  @Test
  public void patchModel_shouldUpdateSelectedFields() {
    // given
    addExemplaryModel();
    final UUID modelId = addedModel.getId();
    // when
    Instant before = currentTimeWithPrecisionToSeconds();
    ModelDTO updatedModel = modelCatalogWriter.patchModel(modelId, PARAMS);
    Instant after = Instant.now();
    // then
    ModelParamsChecker.checkThatModelDTOContainsParamsDTO(updatedModel, PARAMS);
    assertThat(updatedModel.getId()).isEqualTo(modelId);
    assertThat(updatedModel.getAddedBy()).isEqualTo(addedModel.getAddedBy());
    assertThat(updatedModel.getAddedOn()).isEqualTo(addedModel.getAddedOn());
    assertThat(updatedModel.getModifiedBy()).isEqualTo(ITSecurityConfig.USERNAME);
    checkThatIsBetween(addedModel.getModifiedOn(), before, after);
  }

  @Test
  public void patchModel_shouldReturn304_whenNothingToUpdate() {
    addExemplaryModel();
    expectModelCatalogExceptionWithStatus(thrown, HttpStatus.NOT_MODIFIED);
    modelCatalogWriter.patchModel(addedModel.getId(), TestModelParamsBuilder.emptyParamsDTO());
  }

  @Test
  public void patchModel_shouldReturn404_whenModelNotFound() {
    expectModelCatalogExceptionWithStatusAndReason(thrown, HttpStatus.NOT_FOUND);
    modelCatalogWriter.patchModel(UUID.randomUUID(), PARAMS);
  }

  @Test
  public void deleteModel_shouldReturn404_whenModelNotFound() {
    expectModelCatalogExceptionWithStatusAndReason(thrown, HttpStatus.NOT_FOUND);
    modelCatalogWriter.deleteModel(UUID.randomUUID());
  }

  private void addModelAndCheckThatItWasProperlyInitialized() {
    Instant before = currentTimeWithPrecisionToSeconds();
    addExemplaryModel();
    Instant after = Instant.now();
    ModelParamsChecker.checkThatModelDTOContainsParamsDTO(addedModel, PARAMS);
    assertThat(addedModel.getId()).isNotNull();
    assertThat(addedModel.getAddedBy()).isEqualTo(ITSecurityConfig.USERNAME);
    checkThatIsBetween(addedModel.getAddedOn(), before, after);
    assertThat(addedModel.getModifiedBy()).isEqualTo(ITSecurityConfig.USERNAME);
    checkThatIsBetween(addedModel.getModifiedOn(), before, after);
  }

  private void checkThatThereAreNoModelsInDb() {
    assertThat(modelCatalogReader.listModels(ORG_ID)).isEmpty();
  }

  private void checkThatThereIsOneModelInDb() {
    assertThat(modelCatalogReader.listModels(ORG_ID)).hasSize(1);
  }

  private void addExemplaryModel() {
    addedModel = modelCatalogWriter.addModel(PARAMS, ORG_ID);
  }

  private void retrieveModelFromDbAndCompareWithTheAddedOne() {
    ModelDTO retrievedModel = modelCatalogReader.retrieveModel(addedModel.getId());
    assertThat(retrievedModel).isEqualToComparingFieldByFieldRecursively(addedModel);
  }

  private void deleteAddedModel() {
    modelCatalogWriter.deleteModel(addedModel.getId());
  }

  //because of Dates in ModelDTO being formetted with InstantFormatter.DATE_FORMAT
  private Instant currentTimeWithPrecisionToSeconds() {
    Instant now = Instant.now();
    return now.truncatedTo(ChronoUnit.SECONDS);
  }

  private void checkThatIsBetween(String formattedTime, Instant start, Instant end) {
    Instant instant = InstantFormatter.parse(formattedTime);
    assertThat(instant).isGreaterThanOrEqualTo(start);
    assertThat(instant).isLessThanOrEqualTo(end);
  }
}
