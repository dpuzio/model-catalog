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

import org.trustedanalytics.modelcatalog.rest.client.configuration.ApacheClientOrchestrator;
import org.trustedanalytics.modelcatalog.rest.client.http.HttpClientWrapper;
import org.trustedanalytics.modelcatalog.rest.client.http.HttpRequestFactory;
import org.trustedanalytics.modelcatalog.rest.client.http.MultipartRequestsSender;
import org.trustedanalytics.modelcatalog.rest.client.http.OAuthTokenProvider;
import org.trustedanalytics.modelcatalog.rest.client.mapper.DtoJsonMapper;
import org.trustedanalytics.modelcatalog.rest.client.mapper.MapperDefaults;

import org.apache.http.impl.client.CloseableHttpClient;

import java.util.Optional;

public class ModelCatalogClientBuilder {

  private final String url;
  private OAuthTokenProvider tokenProvider;
  private Integer connectionsMaxTotal;
  private Integer connectionsMaxPerRoute;
  private Integer connectionTimeoutMillis;
  private Integer readTimeoutMillis;

  public ModelCatalogClientBuilder(String url) {
    this.url = url;
  }

  public ModelCatalogClientBuilder oAuthTokenProvider(OAuthTokenProvider tokenProvider) {
    this.tokenProvider = tokenProvider;
    return this;
  }

  public ModelCatalogClientBuilder connectionsMaxTotal(int connectionsMaxTotal) {
    this.connectionsMaxTotal = connectionsMaxTotal;
    return this;
  }

  public ModelCatalogClientBuilder connectionsMaxPerRoute(int connectionsMaxPerRoute) {
    this.connectionsMaxPerRoute = connectionsMaxPerRoute;
    return this;
  }

  public ModelCatalogClientBuilder connectionTimeoutMillis(int connectionTimeoutMillis) {
    this.connectionTimeoutMillis = connectionTimeoutMillis;
    return this;
  }

  public ModelCatalogClientBuilder readTimeoutMillis(int readTimeoutMillis) {
    this.readTimeoutMillis = readTimeoutMillis;
    return this;
  }

  public ModelCatalogReaderClient buildReader() {
    CloseableHttpClient httpClient = ApacheClientOrchestrator.prepareHttpClient(
        Optional.ofNullable(tokenProvider),
        Optional.ofNullable(connectionsMaxTotal),
        Optional.ofNullable(connectionsMaxPerRoute),
        Optional.ofNullable(connectionTimeoutMillis),
        Optional.ofNullable(readTimeoutMillis));
    return new ModelCatalogReaderClient(
        new HttpRequestFactory(url),
        new HttpClientWrapper(httpClient),
        new DtoJsonMapper(MapperDefaults.objectMapper()));
  }

  public ModelCatalogWriterClient buildWriter() {
    CloseableHttpClient httpClient = ApacheClientOrchestrator.prepareHttpClient(
        Optional.ofNullable(tokenProvider),
        Optional.ofNullable(connectionsMaxTotal),
        Optional.ofNullable(connectionsMaxPerRoute),
        Optional.ofNullable(connectionTimeoutMillis),
        Optional.ofNullable(readTimeoutMillis));
    HttpRequestFactory requestFactory = new HttpRequestFactory(url);
    MultipartRequestsSender multipartRequestsSender = new MultipartRequestsSender(
        requestFactory,
        new HttpClientWrapper(httpClient),
        new DtoJsonMapper(MapperDefaults.objectMapper()));
    return new ModelCatalogWriterClient(
        requestFactory,
        new HttpClientWrapper(httpClient),
        new DtoJsonMapper(MapperDefaults.objectMapper()),
        multipartRequestsSender);
  }
}
