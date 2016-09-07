/**
 * Copyright (c) 2016 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trustedanalytics.modelcatalog.rest.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import feign.Feign;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

class ClientOrchestrator {

  private static final int CONNECT_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(30);
  private static final int READ_TIMEOUT = (int) TimeUnit.MINUTES.toMillis(5);

  static ModelResource prepareModelResource(String url, Function<Feign.Builder, Feign.Builder> customizations) {
    Objects.requireNonNull(url, "url");
    Objects.requireNonNull(customizations, "customizations");
    ObjectMapper objectMapper = prepareObjectMapper();
    final Feign.Builder builder = customizations.apply(Feign.builder()
                    .encoder(new JacksonEncoder(objectMapper))
                    .decoder(new JacksonDecoder(objectMapper))
                    .options(new Request.Options(CONNECT_TIMEOUT, READ_TIMEOUT))
                    .logger(new Slf4jLogger(ModelResource.class))
                    .logLevel(feign.Logger.Level.BASIC)
                    .client(new OkHttpClient())
    );
    return builder.target(ModelResource.class, url);
  }

  private static ObjectMapper prepareObjectMapper() {
    return new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  }

}
