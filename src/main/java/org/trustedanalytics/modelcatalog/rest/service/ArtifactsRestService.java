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
package org.trustedanalytics.modelcatalog.rest.service;

import org.trustedanalytics.modelcatalog.rest.entities.ArtifactDTO;
import org.trustedanalytics.modelcatalog.service.ArtifactService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

@Service
public class ArtifactsRestService {

  private final ArtifactService artifactService;

  @Autowired
  public ArtifactsRestService(ArtifactService artifactService) {
    this.artifactService = artifactService;
  }

  public ArtifactDTO addArtifact(UUID modelId, Set<String> actions, MultipartFile file) {
    return ArtifactMapper.toArtifactDTO(
            artifactService.addArtifact(
                    modelId, ArtifactMapper.toArtifactActionSet(actions), file));
  }

  public ArtifactDTO retrieveArtifact(UUID modelId, UUID artifactId) {
    return ArtifactMapper.toArtifactDTO(
            artifactService.retrieveArtifact(modelId, artifactId));
  }

  public InputStream retrieveArtifactFile(UUID modelId, UUID artifactId) {
    return artifactService.retrieveArtifactFile(modelId, artifactId);
  }

  public ArtifactDTO deleteArtifact(UUID modelId, UUID artifactId) {
    return ArtifactMapper.toArtifactDTO(
            artifactService.deleteArtifact(modelId, artifactId));
  }

}
