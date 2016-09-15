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
package org.trustedanalytics.modelcatalog.rest.client.configuration;

import org.trustedanalytics.modelcatalog.rest.client.ArtifactResource;
import org.trustedanalytics.modelcatalog.rest.client.ModelResource;

import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

import java.util.Optional;

public class FeignResourceOrchestrator {

  private final Feign.Builder builder;

  public FeignResourceOrchestrator(
          Optional<OAuthTokenProvider> tokenProvider,
          Optional<Integer> connectionTimeoutMillis,
          Optional<Integer> readTimeoutMillis) {
    builder = Feign.builder()
            .encoder(new JacksonEncoder(HttpClientDefaults.objectMapper()))
            .decoder(new JacksonDecoder(HttpClientDefaults.objectMapper()))
            .errorDecoder(new ModelCatalogFeignErrorDecoder())
            .options(new Request.Options(
                    connectionTimeoutMillis.orElse(HttpClientDefaults.CONNECT_TIMEOUT),
                    readTimeoutMillis.orElse(HttpClientDefaults.READ_TIMEOUT)))
            .logger(new Slf4jLogger(ModelResource.class))
            .logLevel(Logger.Level.BASIC)
            .client(new OkHttpClient());
    if (tokenProvider.isPresent()) {
      addOAuthTokenInterceptor(builder, tokenProvider.get());
    }
  }

  public ModelResource prepareModelResource(String url) {
    return builder.target(ModelResource.class, url);
  }

  public ArtifactResource prepareArtifactResource(String url) {
    return builder.target(ArtifactResource.class, url);
  }

  private void addOAuthTokenInterceptor(
          Feign.Builder builder, OAuthTokenProvider tokenProvider) {
    builder.requestInterceptor(requestTemplate -> requestTemplate.header(
            AuthorizationHeader.NAME,
            AuthorizationHeader.value(tokenProvider.provideToken())));
  }
}
