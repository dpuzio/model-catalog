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
import org.trustedanalytics.modelcatalog.rest.RequestParams;
import org.trustedanalytics.modelcatalog.rest.entities.ArtifactActionDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ArtifactDTO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;


public class MultipartRequestsSender {

  private final HttpClient httpclient;
  private final String url;
  private final ObjectMapper objectMapper;

  public MultipartRequestsSender(HttpClient httpclient, String url, ObjectMapper objectMapper) {
    this.httpclient = httpclient;
    this.url = url;
    this.objectMapper = objectMapper;
  }

  public ArtifactDTO postArtifact(UUID modelId,
                                  Set<ArtifactActionDTO> artifactActions,
                                  File artifactFile) {
    HttpPost httpPost = prepareRequest(modelId);
    HttpEntity entity = prepareEntity(artifactActions, artifactFile);
    httpPost.setEntity(entity);
    HttpResponse response = sendRequest(httpPost);
    checkResponseStatusCode(response);
    return extractArtifactDTOentity(response.getEntity());
  }

  private HttpPost prepareRequest(UUID modelId) {
    String path = url + ModelCatalogPaths.pathToModelArtifacts(modelId);
    return new HttpPost(path);
  }

  private HttpEntity prepareEntity(Set<ArtifactActionDTO> artifactActions,
                                   File artifactFile) {
    StringBody json;
    try {
      json = new StringBody(
              objectMapper.writeValueAsString(artifactActions),
              ContentType.APPLICATION_JSON);
    } catch (JsonProcessingException e) {
      throw new ModelCatalogClientFailedException("Exception while preparing request entity", e);
    }
    FileBody file = new FileBody(artifactFile);
    return MultipartEntityBuilder.create()
            .addPart(RequestParams.ARTIFACT_ACTIONS, json)
            .addPart(RequestParams.ARTIFACT_FILE, file)
            .build();
  }

  private HttpResponse sendRequest(HttpPost httpPost) {
    try {
      return httpclient.execute(httpPost);
    } catch (IOException e) {
      throw new ModelCatalogClientFailedException("Exception while sending request", e);
    }
  }

  private void checkResponseStatusCode(HttpResponse response) {
    int statusCode = response.getStatusLine().getStatusCode();
    int expectedStatusCode = HttpStatus.CREATED.value();
    if (statusCode != expectedStatusCode) {
      throw new ModelCatalogClientFailedException("Unsuccessful response status code " +
              "(is: " + statusCode + ", should be " + expectedStatusCode + ")");
    }
  }

  private ArtifactDTO extractArtifactDTOentity(HttpEntity entity) {
    if (null == entity) {
      throw new ModelCatalogClientFailedException("Empty response entity");
    }
    ArtifactDTO artifactDTO;
    try {
      String entityString = EntityUtils.toString(entity);
      artifactDTO = objectMapper.readValue(entityString, ArtifactDTO.class);
    } catch (IOException e) {
      throw new ModelCatalogClientFailedException("Cannot parse response entity", e);
    }
    return artifactDTO;
  }

}
