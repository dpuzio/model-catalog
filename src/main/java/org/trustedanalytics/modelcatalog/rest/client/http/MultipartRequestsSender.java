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
package org.trustedanalytics.modelcatalog.rest.client.http;

import org.trustedanalytics.modelcatalog.rest.ModelCatalogPaths;
import org.trustedanalytics.modelcatalog.rest.RequestParams;
import org.trustedanalytics.modelcatalog.rest.client.ModelCatalogClientException;
import org.trustedanalytics.modelcatalog.rest.client.mapper.DtoJsonMapper;
import org.trustedanalytics.modelcatalog.rest.entities.ArtifactActionDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ArtifactDTO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

public class MultipartRequestsSender {

  private final HttpRequestFactory requestFactory;
  private final HttpClientWrapper httpClientWrapper;
  private final DtoJsonMapper dtoJsonMapper;

  public MultipartRequestsSender(
      HttpRequestFactory requestFactory,
      HttpClientWrapper httpClientWrapper,
      DtoJsonMapper dtoJsonMapper) {
    this.requestFactory = requestFactory;
    this.httpClientWrapper = httpClientWrapper;
    this.dtoJsonMapper = dtoJsonMapper;
  }

  public ArtifactDTO postArtifact(UUID modelId,
                                  Set<ArtifactActionDTO> artifactActions,
                                  InputStream artifactStream,
                                  String artifactFilename) {
    HttpPost httpPost = requestFactory.preparePost(ModelCatalogPaths.pathToModelArtifacts(modelId));
    httpPost.setEntity(prepareEntity(artifactActions, artifactStream, artifactFilename));
    HttpResponse response = httpClientWrapper.execute(httpPost, HttpStatus.CREATED);
    try {
      return dtoJsonMapper.toArtifactDTO(EntityUtils.toString(response.getEntity()));
    } catch (IOException e) {
      throw new ModelCatalogClientException("Cannot parse response entity", e);
    }
  }

  private HttpEntity prepareEntity(Set<ArtifactActionDTO> artifactActions,
                                   InputStream artifactStream,
                                   String artifactFilename) {
    StringBody jsonBody = new StringBody(
        dtoJsonMapper.toJSON(artifactActions),
        ContentType.APPLICATION_JSON);
    InputStreamBody fileBody = new InputStreamBody(
        artifactStream, ContentType.APPLICATION_OCTET_STREAM, artifactFilename);
    return MultipartEntityBuilder.create()
        .addPart(RequestParams.ARTIFACT_ACTIONS, jsonBody)
        .addPart(RequestParams.ARTIFACT_FILE, fileBody)
        .build();
  }
}
