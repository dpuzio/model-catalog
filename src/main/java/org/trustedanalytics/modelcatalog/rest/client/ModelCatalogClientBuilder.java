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
import org.trustedanalytics.modelcatalog.rest.client.configuration.FeignResourceOrchestrator;
import org.trustedanalytics.modelcatalog.rest.client.configuration.OAuthTokenProvider;

import java.util.Optional;

public class ModelCatalogClientBuilder {

  private final String url;
  private OAuthTokenProvider tokenProvider;
  private int connectionTimeoutMillis;
  private int readTimeoutMillis;

  private FeignResourceOrchestrator feignResourceOrchestrator;

  public ModelCatalogClientBuilder(String url) {
    this.url = url;
  }

  public ModelCatalogClientBuilder oAuthTokenProvider(OAuthTokenProvider tokenProvider) {
    this.tokenProvider = tokenProvider;
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
    feignResourceOrchestrator = prepareFeignResourceOrchestrator();
    return new ModelCatalogReaderClient(
            feignResourceOrchestrator.prepareModelResource(url),
            feignResourceOrchestrator.prepareArtifactResource(url));
  }

  public ModelCatalogWriterClient buildWriter() {
    feignResourceOrchestrator = prepareFeignResourceOrchestrator();
    MultipartRequestsSender multipartRequestsSender = ApacheClientOrchestrator
            .prepareMultipartRequestSender(url,
                    Optional.ofNullable(tokenProvider),
                    Optional.ofNullable(connectionTimeoutMillis),
                    Optional.ofNullable(readTimeoutMillis));
    return new ModelCatalogWriterClient(
            feignResourceOrchestrator.prepareModelResource(url),
            feignResourceOrchestrator.prepareArtifactResource(url),
            multipartRequestsSender);
  }

  private FeignResourceOrchestrator prepareFeignResourceOrchestrator() {
    return new FeignResourceOrchestrator(
            Optional.ofNullable(tokenProvider),
            Optional.ofNullable(connectionTimeoutMillis),
            Optional.ofNullable(readTimeoutMillis));
  }

}
