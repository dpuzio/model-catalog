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
import org.trustedanalytics.modelcatalog.rest.entities.ArtifactDTO;

import feign.Param;
import feign.RequestLine;
import org.springframework.core.io.FileSystemResource;

import java.util.UUID;

public interface ArtifactResource {

  @RequestLine("GET " + ModelCatalogPaths.ARTIFACT)
  ArtifactDTO retrieveArtifactMetadata(
          @Param("modelId") UUID modelId, @Param("artifactId") UUID artifactId);

  @RequestLine("GET " + ModelCatalogPaths.ARTIFACT_FILE)
  FileSystemResource retrieveArtifactFile(
          @Param("modelId") UUID modelId, @Param("artifactId") UUID artifactId);

  @RequestLine("DELETE " + ModelCatalogPaths.ARTIFACT)
  ArtifactDTO deleteArtifact(@Param("modelId") UUID modelId, @Param("artifactId") UUID artifactId);

}