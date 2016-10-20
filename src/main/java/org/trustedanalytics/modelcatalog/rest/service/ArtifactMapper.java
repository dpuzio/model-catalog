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

import org.trustedanalytics.modelcatalog.domain.Artifact;
import org.trustedanalytics.modelcatalog.domain.ArtifactAction;
import org.trustedanalytics.modelcatalog.rest.entities.ArtifactDTO;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class ArtifactMapper {

  private ArtifactMapper() {
  }

  public static ArtifactDTO toArtifactDTO(Artifact artifact) {
    return ArtifactDTO.builder()
            .id(artifact.getId())
            .filename(artifact.getFilename())
            .location(artifact.getLocation())
            .actions(toArtifactActionStrings(artifact.getActions()))
            .build();
  }

  public static Set<ArtifactAction> toArtifactActionSet(Set<String> artifactActions) {
    return Optional.ofNullable(artifactActions)
            .orElseGet(Collections::emptySet)
            .stream()
            .map(ArtifactAction::valueOf)
            .collect(Collectors.toSet());
  }

  private static Set<String> toArtifactActionStrings(Set<ArtifactAction> artifactActions) {
    return artifactActions.stream().map(String::valueOf).collect(Collectors.toSet());
  }

}
