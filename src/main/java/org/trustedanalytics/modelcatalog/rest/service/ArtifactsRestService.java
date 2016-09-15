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

import org.trustedanalytics.modelcatalog.rest.entities.ArtifactActionDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ArtifactDTO;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

@Service
public class ArtifactsRestService {

  public ArtifactDTO addArtifact(UUID modelId, Set<ArtifactActionDTO> actions, MultipartFile file) {
    throw new UnsupportedOperationException(); //TODO -> DPNG-10149
  }

  public ArtifactDTO retrieveArtifact(UUID modelId, UUID artifactId) {
    throw new UnsupportedOperationException(); //TODO -> DPNG-10149
  }

  public FileSystemResource retrieveArtifactFile(UUID modelId, UUID artifactId) {
    throw new UnsupportedOperationException(); //TODO -> DPNG-10149
  }

  public ArtifactDTO deleteArtifact(UUID modelId, UUID artifactId) {
    throw new UnsupportedOperationException(); //TODO -> DPNG-10149
  }

}
