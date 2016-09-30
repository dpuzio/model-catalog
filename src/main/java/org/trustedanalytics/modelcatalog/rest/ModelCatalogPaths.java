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

import java.util.UUID;

public class ModelCatalogPaths {

  // @formatter:off

  public static final String API_PREFIX = "/api";
  public static final String API_VERSION = "v1";

  private static final String PREFIX = API_PREFIX + "/" + API_VERSION;

  private static final String MODEL_ID    = "{modelId}";
  private static final String ARTIFACT_ID = "{artifactId}";

  public static final String MODELS        = PREFIX + "/models";
  public static final String MODEL         = PREFIX + "/models/" + MODEL_ID;
  public static final String ARTIFACTS     = PREFIX + "/models/" + MODEL_ID + "/artifacts";
  public static final String ARTIFACT      = PREFIX + "/models/" + MODEL_ID + "/artifacts/" + ARTIFACT_ID;
  public static final String ARTIFACT_FILE = PREFIX + "/models/" + MODEL_ID + "/artifacts/" + ARTIFACT_ID + "/file";

  // @formatter:on

  public static String pathToModel(UUID modelId) {
    return MODEL.replace(MODEL_ID, modelId.toString());
  }

  public static String pathToModelArtifact(UUID modelId, UUID artifactId) {
    return ARTIFACT.replace(MODEL_ID, modelId.toString())
            .replace(ARTIFACT_ID, artifactId.toString());
  }

  public static String pathToModelArtifacts(UUID modelId) {
    return ARTIFACTS.replace(MODEL_ID, modelId.toString());
  }
}
