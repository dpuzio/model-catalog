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

import org.trustedanalytics.modelcatalog.rest.entities.ModelDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ModelModificationParametersDTO;

import java.util.UUID;
import java.util.function.Function;

import feign.Feign;

public class ModelCatalogWriterClient {

  private final ModelResource modelResource;

  /**
   * Creates client applying default configuration
   * @param url endpoint url
   */
  public ModelCatalogWriterClient(String url) {
    modelResource = ClientOrchestrator.prepareModelResource(url, Function.identity());
  }

  /**
   * Creates client applying default configuration and then customizations. Example:
   * <pre>
   * {@code
   * new ModelCatalogReaderClient(apiUrl, builder -> builder.requestInterceptor(template ->
   * template.header("Authorization", "bearer " + token)));
   * }
   * </pre>
   * @param url endpoint url
   * @param customizations custom configuration that should be applied after defaults
   */
  public ModelCatalogWriterClient(String url, Function<Feign.Builder, Feign.Builder> customizations) {
    modelResource = ClientOrchestrator.prepareModelResource(url, customizations);
  }

  public ModelDTO addModel(ModelModificationParametersDTO params, UUID orgId) {
    return modelResource.addModel(params, orgId);
  }

  public ModelDTO updateModel(UUID modelId, ModelModificationParametersDTO params) {
    return modelResource.updateModel(modelId, params);
  }

  public ModelDTO patchModel(UUID modelId, ModelModificationParametersDTO params) {
    return modelResource.patchModel(modelId, params);
  }

  public ModelDTO deleteModel(UUID modelId) {
    return modelResource.deleteModel(modelId);
  }

}
