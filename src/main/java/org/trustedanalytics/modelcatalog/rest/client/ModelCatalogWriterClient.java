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

import org.trustedanalytics.modelcatalog.rest.entities.ArtifactActionDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ArtifactDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ModelDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ModelModificationParametersDTO;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class ModelCatalogWriterClient {

  private final ModelResource modelResource;
  private final ArtifactResource artifactResource;
  private final MultipartRequestsSender multipartRequestsSender;

  ModelCatalogWriterClient(ModelResource modelResource,
                           ArtifactResource artifactResource,
                           MultipartRequestsSender multipartRequestsSender) {
    this.modelResource = modelResource;
    this.artifactResource = artifactResource;
    this.multipartRequestsSender = multipartRequestsSender;
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

  public ArtifactDTO addArtifact(UUID modelId,
                                 List<ArtifactActionDTO> artifactActions,
                                 File artifactFile) {
    return multipartRequestsSender.postArtifact(modelId, artifactActions, artifactFile);
  }

  public ArtifactDTO deleteArtifact(UUID modelId, UUID artifactId) {
    return artifactResource.deleteArtifact(modelId, artifactId);
  }

}
