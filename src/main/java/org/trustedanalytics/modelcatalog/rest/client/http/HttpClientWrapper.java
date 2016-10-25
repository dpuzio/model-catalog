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

import org.trustedanalytics.modelcatalog.rest.client.ModelCatalogClientException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.function.Function;

public class HttpClientWrapper {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final CloseableHttpClient httpClient;

  public HttpClientWrapper(CloseableHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public static void releaseResources(HttpRequestBase request, CloseableHttpResponse response) {
    if (response != null) {
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        EntityUtils.consumeQuietly(entity);
      }

      try {
        response.close();
      } catch (IOException e) {
        // Nothing can be done when there's a problem with closing connection
        LOGGER.warn("Problems with releasing resources", e);
      }
    }

    if (request != null) {
      request.releaseConnection();
    }
  }

  public CloseableHttpResponse execute(HttpRequestBase request) {
    return execute(request, HttpStatus.OK);
  }

  public CloseableHttpResponse execute(HttpRequestBase request, HttpStatus expectedStatus) {
    CloseableHttpResponse response = null;
    try {
      response = httpClient.execute(request);
      checkResponseStatusCode(response, expectedStatus);
      return response;
    } catch (ModelCatalogClientException e) {
      releaseResources(request, response);
      throw e;
    } catch (IOException e) {
      releaseResources(request, response);
      throw new ModelCatalogClientException("Exception while executing request", e);
    }
  }

  public <T> T executeAndMap(
      HttpRequestBase request, HttpStatus expectedStatus, Function<String, T> mapperFunc) {
    CloseableHttpResponse response = execute(request, expectedStatus);
    try {
      HttpEntity entity = response.getEntity();
      if (entity == null) {
        throw new ModelCatalogClientException("Empty response entity");
      }

      return mapperFunc.apply(EntityUtils.toString(entity));
    } catch (IOException e) {
      throw new ModelCatalogClientException("Unable to parse HTTP entity", e);
    } finally {
      HttpClientWrapper.releaseResources(request, response);
    }
  }

  private void checkResponseStatusCode(HttpResponse response, HttpStatus expectedStatus) {
    StatusLine statusLine = response.getStatusLine();
    if (statusLine.getStatusCode() != expectedStatus.value()) {
      throw new ModelCatalogClientException(
          String.format("Unexpected response status code (is: %d %s, expected: %d %s)",
              statusLine.getStatusCode(), statusLine.getReasonPhrase(),
              expectedStatus.value(), expectedStatus.getReasonPhrase()));
    }
  }
}
