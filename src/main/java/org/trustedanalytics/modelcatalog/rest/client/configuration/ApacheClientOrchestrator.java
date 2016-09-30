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

import org.trustedanalytics.modelcatalog.rest.client.MultipartRequestsSender;

import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;

import java.util.Optional;

public class ApacheClientOrchestrator {

  public static MultipartRequestsSender prepareMultipartRequestSender(
          String url,
          Optional<OAuthTokenProvider> tokenProvider,
          Optional<Integer> connectionTimeoutMillis,
          Optional<Integer> readTimeoutMillis) {

    HttpClient client = prepareHttpClient(tokenProvider, connectionTimeoutMillis,
            readTimeoutMillis);
    return new MultipartRequestsSender(client, url, HttpClientDefaults.objectMapper());
  }

  private static HttpClient prepareHttpClient(Optional<OAuthTokenProvider> tokenProvider,
                                              Optional<Integer> connectionTimeoutMillis,
                                              Optional<Integer> readTimeoutMillis) {
    HttpClientBuilder builder = HttpClientBuilder.create()
            .setDefaultRequestConfig(RequestConfig.custom()
                    .setConnectTimeout(connectionTimeoutMillis.orElse(
                            HttpClientDefaults.CONNECT_TIMEOUT))
                    .setConnectionRequestTimeout(readTimeoutMillis.orElse(
                            HttpClientDefaults.READ_TIMEOUT))
                    .build());
    if (tokenProvider.isPresent()) {
      addOAuthTokenInterceptor(builder, tokenProvider.get());
    }
    return builder.build();
  }

  private static void addOAuthTokenInterceptor(
          HttpClientBuilder builder, OAuthTokenProvider tokenProvider) {
    builder.addInterceptorFirst((HttpRequest httpRequest, HttpContext httpContext) -> {
      httpRequest.addHeader(AuthorizationHeader.NAME,
              AuthorizationHeader.value(tokenProvider.provideToken()));
    });
  }

}
