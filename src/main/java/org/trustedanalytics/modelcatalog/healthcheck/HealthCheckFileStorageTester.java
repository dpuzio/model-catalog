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

package org.trustedanalytics.modelcatalog.healthcheck;

import org.trustedanalytics.modelcatalog.storage.files.FileStore;
import org.trustedanalytics.modelcatalog.storage.files.FileStoreException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class HealthCheckFileStorageTester {

  private final FileStore fileStore;
  private static final String TEXT_TO_BE_SAVED = "Some text that should be saved on local storage";
  private static final String FILE_LOCATION = "health-check-location";

  @Autowired
  public HealthCheckFileStorageTester(FileStore fileStore) {
    this.fileStore = fileStore;
  }

  public void verifyFileStore() throws FileStoreException {
    InputStream inputStream = new ByteArrayInputStream(TEXT_TO_BE_SAVED.getBytes(StandardCharsets.UTF_8));
    String uniqueFileLocation = String.format("%s-%s", FILE_LOCATION, UUID.randomUUID());
    fileStore.addFile(uniqueFileLocation, inputStream);
    fileStore.deleteFile(uniqueFileLocation);
  }
}
