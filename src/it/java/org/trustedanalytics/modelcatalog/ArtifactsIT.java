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
import static org.trustedanalytics.modelcatalog.ExpectedExceptionsHelper.*;
import static org.trustedanalytics.modelcatalog.TestFileProvider.testFile;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.trustedanalytics.modelcatalog.rest.ModelCatalogPaths;
import org.trustedanalytics.modelcatalog.rest.client.ModelCatalogClientBuilder;
import org.trustedanalytics.modelcatalog.rest.client.ModelCatalogReaderClient;
import org.trustedanalytics.modelcatalog.rest.client.ModelCatalogWriterClient;
import org.trustedanalytics.modelcatalog.rest.client.http.HttpFileResource;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.UUID;
import javax.annotation.PostConstruct;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, FongoConfig.class})
@WebAppConfiguration
@IntegrationTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles({"integration-test", "in-memory"})
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
  public void client_shouldAddRetrieveAndDeleteArtifactMetadataAndFile() throws IOException {
    // add model
    ModelDTO model = modelCatalogWriter.addModel(
            TestModelParamsBuilder.exemplaryParamsDTO(), UUID.randomUUID());
    final UUID modelId = model.getId();
    // add artifact together with a file
    File f = testFile();
    ArtifactDTO addedArtifact = modelCatalogWriter.addArtifact(
        modelId,
        Collections.singleton(ArtifactActionDTO.PUBLISH_JAR_SCORING_ENGINE),
        new FileInputStream(f),
        f.getName());
    final UUID artifactId = addedArtifact.getId();
    // retrieve metadata
    ArtifactDTO retrievedMetadata = modelCatalogReader.retrieveArtifactMetadata(
            modelId, artifactId);
    assertThat(retrievedMetadata).isEqualToComparingFieldByField(addedArtifact);

    // retrieve artifact file
    HttpFileResource retrievedFile = modelCatalogReader.retrieveArtifactFile(modelId, artifactId);
    assertThat(retrievedFile.getFilename()).isEqualTo(addedArtifact.getFilename());
    byte[] retrievedBytes = readAllBytes(retrievedFile.getInputStream());
    byte[] expectedBytes = Files.readAllBytes(testFile().toPath());
    assertThat(retrievedBytes).isEqualTo(expectedBytes);

    // delete artifact
    ArtifactDTO deletedArtifact = modelCatalogWriter.deleteArtifact(modelId, artifactId);
    assertThat(deletedArtifact).isEqualToComparingFieldByField(addedArtifact);
    // try to retrieve metadata
    expectNotFoundExceptionThrownBy(
        () -> modelCatalogReader.retrieveArtifactMetadata(modelId, artifactId));
    expectNotFoundExceptionThrownBy(
        () -> modelCatalogReader.retrieveArtifactFile(modelId, artifactId));
  }

  @Test
  public void addArtifact_shouldReturn404WhenModelNotFound() throws FileNotFoundException {
    expectModelCatalogExceptionWithStatus(thrown, HttpStatus.NOT_FOUND);
    File f = testFile();
    modelCatalogWriter.addArtifact(UUID.randomUUID(), Collections.EMPTY_SET, new FileInputStream(f), f.getName());
  }

  @Test public void addArtifact_shouldReturn400WhenArtifactFileNotSent() {
    UUID modelId = UUID.randomUUID();
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.ALL.MULTIPART_FORM_DATA);
    RestTemplate restTemplate = new RestTemplate();
    String addArtifactWithoutFileAttached = this.url + ModelCatalogPaths.pathToModelArtifacts(modelId);
    expectHttpClientErrorException(thrown, HttpStatus.BAD_REQUEST);
    ResponseEntity<ArtifactDTO> response = restTemplate.exchange(addArtifactWithoutFileAttached, HttpMethod.POST,
            new HttpEntity(formData, headers), ArtifactDTO.class);
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

  @Test
  public void retrieveArtifactFile_shouldReturn404WhenModelNotFound() {
    expectModelCatalogExceptionWithStatusAndReason(thrown, HttpStatus.NOT_FOUND);
    modelCatalogReader.retrieveArtifactFile(UUID.randomUUID(), UUID.randomUUID());
  }

  @Test
  public void retrieveArtifactFile_shouldReturn404WhenArtifactNotFound() {
    ModelDTO model = modelCatalogWriter.addModel(
        TestModelParamsBuilder.exemplaryParamsDTO(), UUID.randomUUID());
    expectModelCatalogExceptionWithStatusAndReason(thrown, HttpStatus.NOT_FOUND);
    modelCatalogReader.retrieveArtifactFile(model.getId(), UUID.randomUUID());
  }

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

  private byte[] readAllBytes(InputStream is) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    byte[] buf = new byte[16384];
    int count;
    while ((count = is.read(buf, 0, buf.length)) != -1) {
      bos.write(buf, 0, count);
    }

    bos.flush();
    return bos.toByteArray();
  }
}
