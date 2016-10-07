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

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

class TestFileProvider {

  public final static String ARTIFACT_FILENAME = "artifactTestFile";

  static File testFile() {
    try {
      return new ClassPathResource(ARTIFACT_FILENAME).getFile();
    } catch (IOException e) {
      throw new RuntimeException("Cannot obtain test file.");
    }
  }

}
