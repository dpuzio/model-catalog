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

import org.trustedanalytics.modelcatalog.rest.api.ModelCatalogPaths;
import org.trustedanalytics.modelcatalog.rest.entity.Model;

import java.util.Collection;
import java.util.UUID;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

@Headers("Accept: application/json")
interface ModelResource {

  @RequestLine("GET " + ModelCatalogPaths.MODELS + "?orgId={orgId}")
  Collection<Model> listModels(@Param("orgId") UUID orgId);

  @RequestLine("POST " + ModelCatalogPaths.MODELS + "?orgId={orgId}")
  @Headers("Content-Type: application/json")
  Model addModel(Model model, @Param("orgId") UUID orgId);

  @RequestLine("GET " + ModelCatalogPaths.MODEL)
  Model fetchModel(@Param("modelId") UUID modelId);

  @RequestLine("PUT " + ModelCatalogPaths.MODEL)
  @Headers("Content-Type: application/json")
  Model updateModel(@Param("modelId") UUID modelId, Model model);

  @RequestLine("PATCH " + ModelCatalogPaths.MODEL)
  @Headers("Content-Type: application/json")
  Model patchModel(@Param("modelId") UUID modelId, Model model);

  @RequestLine("DELETE " + ModelCatalogPaths.MODEL)
  Model deleteModel(@Param("modelId") UUID modelId);

}
