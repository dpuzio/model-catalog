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

  static final String BEGINNING = "/api";
  static final String API_VERSION = "/v1";

  private static final String MODEL_ID = "{modelId}";
  private static final String ARTIFACT_ID = "{artifactId}";

  public static final String MODELS = BEGINNING + API_VERSION + "/models";
  public static final String MODEL = BEGINNING + API_VERSION + "/models/" + MODEL_ID;
  public static final String ARTIFACTS = MODEL + "/artifacts";
  public static final String ARTIFACT = ARTIFACTS + "/" + ARTIFACT_ID;
  public static final String ARTIFACT_FILE = ARTIFACT + "/file";

  private ModelCatalogPaths() {
  }

  public static String pathToModel(UUID modelId) {
    return replaceModelId(MODEL, modelId);
  }

  public static String pathToModelArtifacts(UUID modelId) {
    return replaceModelId(ARTIFACTS, modelId);
  }

  public static String pathToModelArtifact(UUID modelId, UUID artifactId) {
    return replaceArtifactId(replaceModelId(ARTIFACT, modelId), artifactId);
  }

  private static String replaceModelId(String path, UUID id) {
    return path.replace(MODEL_ID, id.toString());
  }

  private static String replaceArtifactId(String path, UUID id) {
    return path.replace(ARTIFACT_ID, id.toString());
  }

}
