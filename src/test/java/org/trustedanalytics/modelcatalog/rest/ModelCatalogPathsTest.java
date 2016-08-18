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
package org.trustedanalytics.modelcatalog.rest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.trustedanalytics.modelcatalog.rest.ModelCatalogPaths;

import java.util.UUID;

public class ModelCatalogPathsTest {

  @Test
  public void pathToSpecificModel_shouldConsistOfPathToModelsAndModelUUID() {
    String uuidStr = "239b9991-c33d-4a5f-9bf7-3c7a3c770678";
    assertThat(ModelCatalogPaths.pathToSpecificModel(UUID.fromString(uuidStr)))
            .isEqualTo(ModelCatalogPaths.MODELS + "/" + uuidStr);
  }

}