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
import org.trustedanalytics.modelcatalog.rest.client.http.HttpFileResource;
import org.trustedanalytics.modelcatalog.rest.client.http.HttpRequestFactory;
import org.trustedanalytics.modelcatalog.rest.client.mapper.DtoJsonMapper;
import org.trustedanalytics.modelcatalog.rest.entities.ArtifactDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ModelDTO;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;

public class ModelCatalogReaderClient {

  private final HttpRequestFactory requestFactory;
  private final HttpClientWrapper httpClientWrapper;
  private final DtoJsonMapper dtoJsonMapper;

  ModelCatalogReaderClient(
      HttpRequestFactory requestFactory,
      HttpClientWrapper httpClientWrapper,
      DtoJsonMapper dtoJsonMapper) {
    this.requestFactory = requestFactory;
    this.httpClientWrapper = httpClientWrapper;
    this.dtoJsonMapper = dtoJsonMapper;
  }

  public Collection<ModelDTO> listModels(String orgId) {
    return executeGetAndMapResult(
        ModelCatalogPaths.pathToModelsByOrg(orgId), dtoJsonMapper::toModelDTOCollection);
  }

  public ModelDTO retrieveModel(UUID modelId) {
    return executeGetAndMapResult(
        ModelCatalogPaths.pathToModel(modelId), dtoJsonMapper::toModelDTO);
  }

  public ArtifactDTO retrieveArtifactMetadata(UUID modelId, UUID artifactId) {
    return executeGetAndMapResult(
        ModelCatalogPaths.pathToModelArtifact(modelId, artifactId), dtoJsonMapper::toArtifactDTO);
  }

  public HttpFileResource retrieveArtifactFile(UUID modelId, UUID artifactId) {
    HttpRequestBase request = requestFactory.prepareGet(
        ModelCatalogPaths.pathToModelArtifactFile(modelId, artifactId));
    CloseableHttpResponse response = httpClientWrapper.execute(request);
    return new HttpFileResource(request, response, "artifact-" + artifactId);
  }

  private <T> T executeGetAndMapResult(String relativeUri, Function<String, T> mapperFunc) {
    HttpRequestBase request = requestFactory.prepareGet(relativeUri);
    return httpClientWrapper.executeAndMap(request, HttpStatus.OK, mapperFunc);
  }
}
