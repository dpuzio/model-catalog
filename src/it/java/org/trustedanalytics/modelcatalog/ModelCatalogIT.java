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
import static org.hamcrest.CoreMatchers.containsString;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.trustedanalytics.modelcatalog.rest.client.ModelCatalogReaderClient;
import org.trustedanalytics.modelcatalog.rest.client.ModelCatalogWriterClient;
import org.trustedanalytics.modelcatalog.rest.entities.ModelDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ModelModificationParametersDTO;
import org.trustedanalytics.modelcatalog.rest.service.InstantFormatter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import javax.annotation.PostConstruct;

import feign.FeignException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, FongoConfig.class})
@WebAppConfiguration
@IntegrationTest("server.port:0")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("integration-test")
public class ModelCatalogIT {

  @Value("http://localhost:${local.server.port}")
  private String url;

  private ModelCatalogReaderClient modelCatalogReader;
  private ModelCatalogWriterClient modelCatalogWriter;

  private final UUID ORG_ID = UUID.randomUUID();
  private final ModelModificationParametersDTO params = TestModelParamsBuilder.exemplaryParamsDTO();
  private ModelDTO addedModel;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @PostConstruct
  public void init() {
    modelCatalogReader = new ModelCatalogReaderClient(url);
    modelCatalogWriter = new ModelCatalogWriterClient(url);
  }

  @Test
  public void shouldListAddRetrieveAndDeleteModels() {
    checkThatThereAreNoModelsInDb();
    addModelAndCheckThatItWasProperlyInitialized();
    checkThatThereIsOneModelInDb();
    retrieveModelFromDbAndCompareWithTheAddedOne();
    deleteAddedModel();
    checkThatThereAreNoModelsInDb();
  }

  @Test
  public void retrieveModel_shouldReturn404WhenModelNotFound() {
    expectFeignExceptionWithStatusAndReason(HttpStatus.NOT_FOUND);
    modelCatalogReader.retrieveModel(UUID.randomUUID());
  }

  @Test
  public void shouldUpdateModel() {
    // given
    addEmptyModel();
    final UUID modelId = addedModel.getId();
    // when
    Instant before = currentTimeWithPrecisionToMinutes();
    ModelDTO updatedModel = modelCatalogWriter.updateModel(modelId, params);
    Instant after = Instant.now();
    // then
    ModelParamsChecker.checkThatModelDTOContainsParamsDTO(updatedModel, params);
    assertThat(updatedModel.getId()).isEqualTo(modelId);
    assertThat(updatedModel.getAddedBy()).isEqualTo(addedModel.getAddedBy());
    assertThat(updatedModel.getAddedOn()).isEqualTo(addedModel.getAddedOn());
    assertThat(updatedModel.getModifiedBy()).isEqualTo(ITSecurityConfig.USERNAME);
    checkThatIsBetween(updatedModel.getModifiedOn(), before, after);
  }

  @Test
  public void updateModel_shouldReturn404WhenModelNotFound() {
    expectFeignExceptionWithStatusAndReason(HttpStatus.NOT_FOUND);
    modelCatalogWriter.updateModel(UUID.randomUUID(), params);
  }

  @Test
  public void shouldPatchModel() {
    // given
    addEmptyModel();
    final UUID modelId = addedModel.getId();
    // when
    Instant before = currentTimeWithPrecisionToMinutes();
    ModelDTO updatedModel = modelCatalogWriter.patchModel(modelId, params);
    Instant after = Instant.now();
    // then
    ModelParamsChecker.checkThatModelDTOContainsParamsDTO(updatedModel, params);
    assertThat(updatedModel.getId()).isEqualTo(modelId);
    assertThat(updatedModel.getAddedBy()).isEqualTo(addedModel.getAddedBy());
    assertThat(updatedModel.getAddedOn()).isEqualTo(addedModel.getAddedOn());
    assertThat(updatedModel.getModifiedBy()).isEqualTo(ITSecurityConfig.USERNAME);
    checkThatIsBetween(addedModel.getModifiedOn(), before, after);
  }

  @Test
  public void patchModel_shouldReturn304WhenNothingToUpdate() {
    addExemplaryModel();
    expectFeignExceptionWithStatus(HttpStatus.NOT_MODIFIED);
    modelCatalogWriter.patchModel(addedModel.getId(), TestModelParamsBuilder.emptyParamsDTO());
  }

  @Test
  public void patchModel_shouldReturn404WhenModelNotFound() {
    expectFeignExceptionWithStatusAndReason(HttpStatus.NOT_FOUND);
    modelCatalogWriter.patchModel(UUID.randomUUID(), params);
  }

  @Test
  public void deleteModel_shouldReturn404WhenModelNotFound() {
    expectFeignExceptionWithStatusAndReason(HttpStatus.NOT_FOUND);
    modelCatalogWriter.deleteModel(UUID.randomUUID());
  }

  private void addModelAndCheckThatItWasProperlyInitialized() {
    Instant before = currentTimeWithPrecisionToMinutes();
    addExemplaryModel();
    Instant after = Instant.now();
    ModelParamsChecker.checkThatModelDTOContainsParamsDTO(addedModel, params);
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
    addedModel = modelCatalogWriter.addModel(params, ORG_ID);
  }

  private void addEmptyModel() {
    ModelModificationParametersDTO emptyParams = TestModelParamsBuilder.emptyParamsDTO();
    addedModel = modelCatalogWriter.addModel(emptyParams, ORG_ID);
  }

  private void retrieveModelFromDbAndCompareWithTheAddedOne() {
    ModelDTO retrievedModel = modelCatalogReader.retrieveModel(addedModel.getId());
    assertThat(retrievedModel).isEqualToComparingFieldByFieldRecursively(addedModel);
  }

  private void deleteAddedModel() {
    modelCatalogWriter.deleteModel(addedModel.getId());
  }

  private void expectFeignExceptionWithStatusAndReason(HttpStatus status) {
    expectFeignExceptionWithStatus(status);
    thrown.expectMessage(containsString(status.getReasonPhrase()));
  }

  private void expectFeignExceptionWithStatus(HttpStatus status) {
    thrown.expect(FeignException.class);
    thrown.expectMessage(containsString(status.toString()));
  }

  //because of Dates in ModelDTO being formetted with InstantFormatter.DATE_FORMAT
  private Instant currentTimeWithPrecisionToMinutes() {
    Instant now = Instant.now();
    return now.truncatedTo(ChronoUnit.MINUTES);
  }

  private void checkThatIsBetween(String formattedTime, Instant start, Instant end) {
    Instant instant = InstantFormatter.parse(formattedTime);
    assertThat(instant).isGreaterThanOrEqualTo(start);
    assertThat(instant).isLessThanOrEqualTo(end);
  }

}
