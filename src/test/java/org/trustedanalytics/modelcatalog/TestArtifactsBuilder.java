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
package org.trustedanalytics.modelcatalog;

import org.trustedanalytics.modelcatalog.domain.Artifact;
import org.trustedanalytics.modelcatalog.domain.ArtifactAction;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

class TestArtifactsBuilder {

  public static final UUID ARTIFACT_ID = UUID.randomUUID();
  public static final String ARTIFACT_LOCATION =
          String.format("/%s/%s", TestModelsBuilder.ID, ARTIFACT_ID);
  public static final String ARTIFACT_FILENAME = "model-0.0.1.jar";

  public static Artifact exemplaryArtifact() {
    Set<ArtifactAction> actions = new HashSet<>();
    actions.add(ArtifactAction.PUBLISH_JAR_SCORING_ENGINE);
    return Artifact.builder()
            .id(ARTIFACT_ID)
            .filename(ARTIFACT_FILENAME)
            .location(ARTIFACT_LOCATION)
            .actions(actions)
            .build();
  }

}
