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
import org.trustedanalytics.modelcatalog.rest.entities.ModelDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ModelModificationParametersDTO;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.Collection;
import java.util.UUID;

@Headers("Accept: application/json")
public interface ModelResource {

  @RequestLine("GET " + ModelCatalogPaths.MODELS + "?orgId={orgId}")
  Collection<ModelDTO> listModels(@Param("orgId") UUID orgId);

  @RequestLine("POST " + ModelCatalogPaths.MODELS + "?orgId={orgId}")
  @Headers("Content-Type: application/json")
  ModelDTO addModel(ModelModificationParametersDTO params, @Param("orgId") UUID orgId);

  @RequestLine("GET " + ModelCatalogPaths.MODEL)
  ModelDTO fetchModel(@Param("modelId") UUID modelId);

  @RequestLine("PUT " + ModelCatalogPaths.MODEL)
  @Headers("Content-Type: application/json")
  ModelDTO updateModel(@Param("modelId") UUID modelId, ModelModificationParametersDTO params);

  @RequestLine("PATCH " + ModelCatalogPaths.MODEL)
  @Headers("Content-Type: application/json")
  ModelDTO patchModel(@Param("modelId") UUID modelId, ModelModificationParametersDTO params);

  @RequestLine("DELETE " + ModelCatalogPaths.MODEL)
  ModelDTO deleteModel(@Param("modelId") UUID modelId);

}
