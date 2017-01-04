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
package org.trustedanalytics.modelcatalog.rest.client;

import org.trustedanalytics.modelcatalog.rest.ModelCatalogPaths;
import org.trustedanalytics.modelcatalog.rest.client.http.HttpClientWrapper;
import org.trustedanalytics.modelcatalog.rest.client.http.HttpRequestFactory;
import org.trustedanalytics.modelcatalog.rest.client.http.MultipartRequestsSender;
import org.trustedanalytics.modelcatalog.rest.client.mapper.DtoJsonMapper;
import org.trustedanalytics.modelcatalog.rest.entities.ArtifactActionDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ArtifactDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ModelDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ModelModificationParametersDTO;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.http.HttpStatus;

import java.io.InputStream;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public class ModelCatalogWriterClient {

  private final HttpRequestFactory requestFactory;
  private final HttpClientWrapper httpClientWrapper;
  private final DtoJsonMapper dtoJsonMapper;
  private final MultipartRequestsSender multipartRequestsSender;

  ModelCatalogWriterClient(HttpRequestFactory requestFactory,
                           HttpClientWrapper httpClientWrapper,
                           DtoJsonMapper dtoJsonMapper,
                           MultipartRequestsSender multipartRequestsSender) {
    this.requestFactory = requestFactory;
    this.httpClientWrapper = httpClientWrapper;
    this.dtoJsonMapper = dtoJsonMapper;
    this.multipartRequestsSender = multipartRequestsSender;
  }

  public ModelDTO addModel(ModelModificationParametersDTO params, String orgId) {
    HttpPost request = requestFactory.preparePost(ModelCatalogPaths.pathToModelsByOrg(orgId));
    return executeWithParams(
        request, HttpStatus.CREATED, params, dtoJsonMapper::toModelDTO);
  }

  public ModelDTO updateModel(UUID modelId, ModelModificationParametersDTO params) {
    HttpPut request = requestFactory.preparePut(ModelCatalogPaths.pathToModel(modelId));
    return executeWithParams(
        request, HttpStatus.OK, params, dtoJsonMapper::toModelDTO);
  }

  public ModelDTO patchModel(UUID modelId, ModelModificationParametersDTO params) {
    HttpPatch request = requestFactory.preparePatch(ModelCatalogPaths.pathToModel(modelId));
    return executeWithParams(
        request, HttpStatus.OK, params, dtoJsonMapper::toModelDTO);
  }

  public ModelDTO deleteModel(UUID modelId) {
    HttpDelete request = requestFactory.prepareDelete(ModelCatalogPaths.pathToModel(modelId));
    return httpClientWrapper.executeAndMap(
        request, HttpStatus.OK, dtoJsonMapper::toModelDTO);
  }

  public ArtifactDTO addArtifact(UUID modelId,
                                 Set<ArtifactActionDTO> artifactActions,
                                 InputStream artifactStream,
                                 String artifactFilename) {
    return multipartRequestsSender.postArtifact(
        modelId, artifactActions, artifactStream, artifactFilename);
  }

  public ArtifactDTO deleteArtifact(UUID modelId, UUID artifactId) {
    HttpDelete request =
        requestFactory.prepareDelete(ModelCatalogPaths.pathToModelArtifact(modelId, artifactId));
    return httpClientWrapper.executeAndMap(
        request, HttpStatus.OK, dtoJsonMapper::toArtifactDTO);
  }

  private <T> T executeWithParams(
      HttpEntityEnclosingRequestBase request,
      HttpStatus expectedStatus,
      Object params,
      Function<String, T> mapperFunc) {
    request.setEntity(new StringEntity(dtoJsonMapper.toJSON(params), ContentType.APPLICATION_JSON));
    return httpClientWrapper.executeAndMap(request, expectedStatus, mapperFunc);
  }
}
