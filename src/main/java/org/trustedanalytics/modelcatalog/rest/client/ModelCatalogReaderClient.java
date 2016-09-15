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

import org.trustedanalytics.modelcatalog.rest.entities.ArtifactDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ModelDTO;

import org.springframework.core.io.FileSystemResource;

import java.util.Collection;
import java.util.UUID;

public class ModelCatalogReaderClient {

  private final ModelResource modelResource;
  private final ArtifactResource artifactResource;

  ModelCatalogReaderClient(ModelResource modelResource, ArtifactResource artifactResource) {
    this.modelResource = modelResource;
    this.artifactResource = artifactResource;
  }

  public Collection<ModelDTO> listModels(UUID orgId) {
    return modelResource.listModels(orgId);
  }

  public ModelDTO retrieveModel(UUID modelId) {
    return modelResource.fetchModel(modelId);
  }

  public ArtifactDTO retrieveArtifactMetadata(UUID modelId, UUID artifactId) {
    return artifactResource.retrieveArtifactMetadata(modelId, artifactId);
  }

  public FileSystemResource retrieveArtifactFile(UUID modelId, UUID artifactId) {
    return artifactResource.retrieveArtifactFile(modelId, artifactId);
  }

}
