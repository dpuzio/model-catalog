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
import org.trustedanalytics.modelcatalog.domain.Model;
import org.trustedanalytics.modelcatalog.rest.client.ModelCatalogReaderClient;
import org.trustedanalytics.modelcatalog.rest.client.ModelCatalogWriterClient;

import java.util.UUID;

import javax.annotation.PostConstruct;

import feign.FeignException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, FongoConfig.class})
@WebAppConfiguration
@IntegrationTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles({"integration-test"})
public class ModelCatalogIT {

  @Value("http://localhost:${server.port}")
  private String url;

  private ModelCatalogReaderClient modelCatalogReader;
  private ModelCatalogWriterClient modelCatalogWriter;

  private final UUID ORG_ID = UUID.randomUUID();
  private Model addedModel;

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
    addExemplaryModel();
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
    addExemplaryModel();
    final UUID modelId = addedModel.getId();
    // when
    Model updatedModel = modelCatalogWriter.updateModel(modelId, new Model());
    // then
    Model expected = newModelWithId(modelId);
    assertThat(updatedModel).isEqualToComparingFieldByFieldRecursively(expected);
  }

  @Test
  public void updateModel_shouldReturn404WhenModelNotFound() {
    expectFeignExceptionWithStatusAndReason(HttpStatus.NOT_FOUND);
    modelCatalogWriter.updateModel(UUID.randomUUID(), new Model());
  }

  @Test
  public void updateModel_shouldReturn422WhenIdsMismatch() {
    addExemplaryModel();
    expectFeignExceptionWithStatusAndReason(HttpStatus.UNPROCESSABLE_ENTITY);
    modelCatalogWriter.updateModel(UUID.randomUUID(), newModelWithId(UUID.randomUUID()));
  }

  @Test
  public void shouldPatchModel() {
    // given
    addExemplaryModel();
    Model modelPatch = new Model();
    final String newDescription = "New description.";
    modelPatch.setDescription(newDescription);
    // when
    Model updatedModel = modelCatalogWriter.patchModel(addedModel.getId(), modelPatch);
    // then
    Model expected = addedModel;
    expected.setDescription(newDescription);
    assertThat(updatedModel).isEqualToComparingFieldByFieldRecursively(expected);
  }

  @Test
  public void patchModel_shouldReturn304WhenNothingToUpdate() {
    addExemplaryModel();
    expectFeignExceptionWithStatus(HttpStatus.NOT_MODIFIED);
    modelCatalogWriter.patchModel(addedModel.getId(), new Model());
  }

  @Test
  public void patchModel_shouldReturn404WhenModelNotFound() {
    expectFeignExceptionWithStatusAndReason(HttpStatus.NOT_FOUND);
    modelCatalogWriter.patchModel(UUID.randomUUID(), new Model());
  }

  @Test
  public void patchModel_shouldReturn422WhenIdsMismatch() {
    addExemplaryModel();
    expectFeignExceptionWithStatusAndReason(HttpStatus.UNPROCESSABLE_ENTITY);
    modelCatalogWriter.patchModel(UUID.randomUUID(), newModelWithId(UUID.randomUUID()));
  }

  @Test
  public void deleteModel_shouldReturn404WhenModelNotFound() {
    expectFeignExceptionWithStatusAndReason(HttpStatus.NOT_FOUND);
    modelCatalogWriter.deleteModel(UUID.randomUUID());
  }

  private void checkThatThereAreNoModelsInDb() {
    assertThat(modelCatalogReader.listModels(ORG_ID)).isEmpty();
  }

  private void checkThatThereIsOneModelInDb() {
    assertThat(modelCatalogReader.listModels(ORG_ID)).hasSize(1);
  }

  private void addExemplaryModel() {
    Model exemplaryModel = TestModelsBuilder.prepareExemplaryModel();
    addedModel = modelCatalogWriter.addModel(exemplaryModel, ORG_ID);
  }

  private void retrieveModelFromDbAndCompareWithTheAddedOne() {
    Model retrievedModel = modelCatalogReader.retrieveModel(addedModel.getId());
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

  private Model newModelWithId(UUID uuid) {
    Model model = new Model();
    model.setId(uuid);
    return model;
  }

}
