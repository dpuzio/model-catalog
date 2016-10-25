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
package org.trustedanalytics.modelcatalog.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

@Repository
@Profile("local")
public class LocalFileStore implements FileStore {

  private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileStore.class);

  private final Path basePath;
  private final FileHelpers fileHelpers;

  @Autowired
  public LocalFileStore(Path localStorageBasePath, FileHelpers fileHelpers) throws IOException {
    basePath = localStorageBasePath;
    this.fileHelpers = fileHelpers;
    fileHelpers.ensureDirectoryExists(basePath);
  }

  @Override
  public InputStream retrieveFile(String location) throws FileStoreException {
    Path path = fileHelpers.resolvePath(basePath, location);
    if (!fileHelpers.exists(path)) {
      throw new FileStoreException("Unable to find file: " + location);
    }
    try {
      return fileHelpers.readFile(path);
    } catch (IOException e) {
      throw new FileStoreException("Unable to read file: " + location, e);
    }
  }

  @Override
  public void addFile(String location, InputStream data) throws FileStoreException {
    Path path = fileHelpers.resolvePath(basePath, location);
    try {
      fileHelpers.ensureDirectoryExists(path.getParent());

      OutputStream outputStream = fileHelpers.createNewFile(path);
      fileHelpers.copyStream(data, outputStream);
    } catch (IOException e) {
      throw new FileStoreException("Unable to store file: " + location, e);
    }
  }

  @Override
  public void deleteFile(String location) throws FileStoreException {
    Path path = fileHelpers.resolvePath(basePath, location);
    if (!fileHelpers.exists(path)) {
      LOGGER.warn("File does not exist: " + location);
      return;
    }
    try {
      fileHelpers.delete(path);
    } catch (IOException e) {
      throw new FileStoreException("Unable to delete file: " + location, e);
    }
  }
}
