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
package org.trustedanalytics.modelcatalog.service;

public enum ModelServiceExceptionCode {
  CANNOT_MAP_PROPERTIES,
  MODEL_NOT_FOUND,
  MODEL_LIST_FAILED,
  MODEL_RETRIEVE_FAILED,
  MODEL_ADD_FAILED,
  MODEL_DELETE_FAILED,
  MODEL_NOTHING_TO_UPDATE,
  MODEL_UPDATE_FAILED,
  ARTIFACT_NOT_FOUND,
  ARTIFACT_FILE_RETRIEVE_FAILED,
  ARTIFACT_ADD_FAILED,
  ARTIFACT_DELETE_FAILED,
  REQUIRED_FIELDS_MISSING,
}
