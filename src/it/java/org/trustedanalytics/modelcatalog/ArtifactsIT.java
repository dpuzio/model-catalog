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
        .expectModelCatalogExceptionWithStatus;
import static org.trustedanalytics.modelcatalog.ExpectedExceptionsHelper
        .expectModelCatalogExceptionWithStatusAndReason;
import static org.trustedanalytics.modelcatalog.ExpectedExceptionsHelper
        .expectNotFoundExceptionThrownBy;
import static org.trustedanalytics.modelcatalog.TestFileProvider.testFile;

import org.trustedanalytics.modelcatalog.rest.client.ModelCatalogClientBuilder;
import org.trustedanalytics.modelcatalog.rest.client.ModelCatalogReaderClient;
import org.trustedanalytics.modelcatalog.rest.client.ModelCatalogWriterClient;
import org.trustedanalytics.modelcatalog.rest.entities.ArtifactActionDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ArtifactDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ModelDTO;

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

import java.util.Collections;
import java.util.UUID;
import javax.annotation.PostConstruct;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, FongoConfig.class})
@WebAppConfiguration
@IntegrationTest("server.port:0")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("integration-test")
public class ArtifactsIT {

  @Value("http://localhost:${local.server.port}")
  private String url;
  private ModelCatalogReaderClient modelCatalogReader;
  private ModelCatalogWriterClient modelCatalogWriter;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @PostConstruct
  public void init() {
    modelCatalogReader = new ModelCatalogClientBuilder(url).buildReader();
    modelCatalogWriter = new ModelCatalogClientBuilder(url).buildWriter();
  }

  @Test
  public void shouldAddRetrieveAndDeleteArtifactMetadataAndFile() {
    // add model
    ModelDTO model = modelCatalogWriter.addModel(
            TestModelParamsBuilder.exemplaryParamsDTO(), UUID.randomUUID());
    final UUID modelId = model.getId();
    // add artifact together with a file
    ArtifactDTO addedArtifact = modelCatalogWriter.addArtifact(
            modelId,
            Collections.singleton(ArtifactActionDTO.DOWNLOAD),
            testFile());
    final UUID artifactId = addedArtifact.getId();
    // retrieve metadata
    ArtifactDTO retrievedMetadata = modelCatalogReader.retrieveArtifactMetadata(
            modelId, artifactId);
    assertThat(retrievedMetadata).isEqualToComparingFieldByField(addedArtifact);
    // retrieve file TODO DPNG-10563
//    FileSystemResource retrievedFile = modelCatalogReader.retrieveArtifactFile(modelId,
// artifactId);
//    assertThat(retrievedFile.getFilename()).isEqualTo(ARTIFACT_FILENAME);
//    assertThat(retrievedFile.contentLength()).isEqualTo(13);
    // delete artifact
    ArtifactDTO deletedArtifact = modelCatalogWriter.deleteArtifact(modelId, artifactId);
    assertThat(deletedArtifact).isEqualToComparingFieldByField(addedArtifact);
    // try to retrieve metadata
    expectNotFoundExceptionThrownBy(
            () -> modelCatalogReader.retrieveArtifactMetadata(modelId, artifactId));
    // try to retrieve file -> TODO DPNG-10563
  }

  @Test
  public void addArtifact_shouldReturn404WhenModelNotFound() {
    expectModelCatalogExceptionWithStatus(thrown, HttpStatus.NOT_FOUND);
    modelCatalogWriter.addArtifact(UUID.randomUUID(), Collections.EMPTY_SET, testFile());
  }

  @Test
  public void retrieveArtifactMetadata_shouldReturn404WhenModelNotFound() {
    expectModelCatalogExceptionWithStatusAndReason(thrown, HttpStatus.NOT_FOUND);
    modelCatalogReader.retrieveArtifactMetadata(UUID.randomUUID(), UUID.randomUUID());
  }

  @Test
  public void retrieveArtifactMetadata_shouldReturn404WhenArtifactNotFound() {
    ModelDTO model = modelCatalogWriter.addModel(
            TestModelParamsBuilder.exemplaryParamsDTO(), UUID.randomUUID());
    expectModelCatalogExceptionWithStatusAndReason(thrown, HttpStatus.NOT_FOUND);
    modelCatalogReader.retrieveArtifactMetadata(model.getId(), UUID.randomUUID());
  }

//  @Test TODO DPNG-10563
//  public void retrieveArtifactFileShould_shouldReturn404WhenModelNotFound() {
//    expectModelCatalogExceptionWithStatusAndReason(HttpStatus.NOT_FOUND);
//    modelCatalogReader.retrieveArtifactFile(UUID.randomUUID(), UUID.randomUUID());
//  }

//  @Test TODO DPNG-10563
//  public void retrieveArtifactFileShould_shouldReturn404WhenArtifactNotFound() {
//    ModelDTO model = modelCatalogWriter.addModel(
//            TestModelParamsBuilder.exemplaryParamsDTO(), UUID.randomUUID());
//    expectModelCatalogExceptionWithStatusAndReason(HttpStatus.NOT_FOUND);
//    modelCatalogReader.retrieveArtifactFile(model.getId(), UUID.randomUUID());
//  }

//  @Test TODO DPNG-10563
//  public void deleteArtifactMetadata_shouldDeleteArtifactFile() {
//
//  }

  @Test
  public void deleteArtifact_shouldReturn404WhenModelNotFound() {
    expectModelCatalogExceptionWithStatusAndReason(thrown, HttpStatus.NOT_FOUND);
    modelCatalogWriter.deleteArtifact(UUID.randomUUID(), UUID.randomUUID());
  }

  @Test
  public void deleteArtifact_shouldReturn404WhenArtifactNotFound() {
    ModelDTO model = modelCatalogWriter.addModel(
            TestModelParamsBuilder.exemplaryParamsDTO(), UUID.randomUUID());
    expectModelCatalogExceptionWithStatusAndReason(thrown, HttpStatus.NOT_FOUND);
    modelCatalogWriter.deleteArtifact(model.getId(), UUID.randomUUID());
  }

}
