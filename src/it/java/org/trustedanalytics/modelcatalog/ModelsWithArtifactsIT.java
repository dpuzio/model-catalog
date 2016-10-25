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
import static org.trustedanalytics.modelcatalog.ExpectedExceptionsHelper
    .expectNotFoundExceptionThrownBy;
import static org.trustedanalytics.modelcatalog.TestFileProvider.testFile;

import org.trustedanalytics.modelcatalog.rest.client.ModelCatalogClientBuilder;
import org.trustedanalytics.modelcatalog.rest.client.ModelCatalogReaderClient;
import org.trustedanalytics.modelcatalog.rest.client.ModelCatalogWriterClient;
import org.trustedanalytics.modelcatalog.rest.entities.ArtifactActionDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ArtifactDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ModelDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ModelModificationParametersDTO;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.UUID;
import javax.annotation.PostConstruct;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, FongoConfig.class})
@WebAppConfiguration
@IntegrationTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles({"integration-test", "in-memory"})
public class ModelsWithArtifactsIT {

  @Value("http://localhost:${local.server.port}")
  private String url;

  private ModelCatalogReaderClient modelCatalogReader;
  private ModelCatalogWriterClient modelCatalogWriter;

  private final ModelModificationParametersDTO params = TestModelParamsBuilder.exemplaryParamsDTO();
  private UUID modelId;
  private ArtifactDTO artifact1, artifact2, artifact3;

  @PostConstruct
  public void init() {
    modelCatalogReader = new ModelCatalogClientBuilder(url).buildReader();
    modelCatalogWriter = new ModelCatalogClientBuilder(url).buildWriter();
  }

  @Before
  public void setUp() {
    ModelDTO model = modelCatalogWriter.addModel(params, UUID.randomUUID());
    modelId = model.getId();
  }

  @Test
  public void addingArtifacts_shouldAddArtifactsToModelArtifactsList()
      throws FileNotFoundException {
    addArtifacts();
    ModelDTO retrievedModel = modelCatalogReader.retrieveModel(modelId);
    assertThat(retrievedModel.getArtifacts())
            .usingFieldByFieldElementComparator()
            .contains(artifact1, artifact2, artifact3);
  }

  @Test
  public void deletingArtifacts_shouldDeleteArtifactsFromModelArtifactsList()
      throws FileNotFoundException {
    addArtifacts();
    modelCatalogWriter.deleteArtifact(modelId, artifact2.getId());
    ModelDTO retrievedModel = modelCatalogReader.retrieveModel(modelId);
    assertThat(retrievedModel.getArtifacts())
            .usingFieldByFieldElementComparator()
            .contains(artifact1, artifact3);
    modelCatalogWriter.deleteArtifact(modelId, artifact1.getId());
    modelCatalogWriter.deleteArtifact(modelId, artifact3.getId());
    retrievedModel = modelCatalogReader.retrieveModel(modelId);
    assertThat(retrievedModel.getArtifacts()).isEmpty();
  }

  @Test
  public void deletingModel_shouldDeleteArtifactsMetadataAndFiles() throws FileNotFoundException {
    addArtifacts();
    modelCatalogWriter.deleteModel(modelId);
    expectNotFoundExceptionThrownBy(
            () -> modelCatalogReader.retrieveArtifactMetadata(modelId, artifact1.getId()));
    expectNotFoundExceptionThrownBy(
            () -> modelCatalogReader.retrieveArtifactMetadata(modelId, artifact2.getId()));
    expectNotFoundExceptionThrownBy(
            () -> modelCatalogReader.retrieveArtifactMetadata(modelId, artifact3.getId()));
    expectNotFoundExceptionThrownBy(
        () -> modelCatalogReader.retrieveArtifactFile(modelId, artifact1.getId()));
    expectNotFoundExceptionThrownBy(
        () -> modelCatalogReader.retrieveArtifactFile(modelId, artifact2.getId()));
    expectNotFoundExceptionThrownBy(
        () -> modelCatalogReader.retrieveArtifactFile(modelId, artifact3.getId()));
  }

  private void addArtifacts() throws FileNotFoundException {
    File f = testFile();
    artifact1 = modelCatalogWriter.addArtifact(
        modelId, Collections.emptySet(), new FileInputStream(f), f.getName());
    artifact2 = modelCatalogWriter.addArtifact(modelId,
            Collections.singleton(ArtifactActionDTO.PUBLISH_TO_MARKETPLACE), new FileInputStream(f), f.getName());
    artifact3 = modelCatalogWriter.addArtifact(modelId,
            Collections.singleton(ArtifactActionDTO.PUBLISH_TO_TAP_SCORING_ENGINE), new FileInputStream(f), f.getName());
  }
}
