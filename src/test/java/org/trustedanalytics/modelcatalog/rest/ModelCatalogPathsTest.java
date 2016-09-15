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
package org.trustedanalytics.modelcatalog.rest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import java.util.UUID;

public class ModelCatalogPathsTest {

  @Test
  public void pathToModel_shouldConsistOfPathToModelsAndModelUUID() {
    UUID uuid = UUID.randomUUID();
    assertThat(ModelCatalogPaths.pathToModel(uuid))
            .isEqualTo(ModelCatalogPaths.MODELS + "/" + uuid);
  }

  @Test
  public void pathToModelArtifacts_shouldConsistOfPathToModelPlusArtifactsString() {
    UUID uuid = UUID.randomUUID();
    assertThat(ModelCatalogPaths.pathToModelArtifacts(uuid))
            .isEqualTo(ModelCatalogPaths.pathToModel(uuid) + "/artifacts");
  }

  @Test
  public void
  pathToModelArtifact_shouldConsistOfPathToModelPlusArtifactStringAndArtifactId() {
    UUID modelId = UUID.randomUUID();
    UUID artifactId = UUID.randomUUID();
    assertThat(ModelCatalogPaths.pathToModelArtifact(modelId, artifactId))
            .isEqualTo(ModelCatalogPaths.pathToModel(modelId) + "/artifacts/" + artifactId);
  }

}