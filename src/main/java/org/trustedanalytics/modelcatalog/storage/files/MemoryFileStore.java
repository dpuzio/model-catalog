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
package org.trustedanalytics.modelcatalog.storage.files;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Repository
@Profile({"in-memory"})
public class MemoryFileStore implements FileStore {

  private static final Logger LOGGER = LoggerFactory.getLogger(MemoryFileStore.class);

  private final Map<String, byte[]> files = new HashMap<>();

  @Override
  public InputStream retrieveFile(String location) throws FileStoreException {
    byte[] bytes = files.getOrDefault(location, null);
    if (bytes == null) {
      throw new FileStoreException("Unable to find file: " + location);
    }

    return new ByteArrayInputStream(bytes);
  }

  @Override
  public void addFile(String location, InputStream data) throws FileStoreException {
    ByteArrayOutputStream ostream = new ByteArrayOutputStream();
    try {
      IOUtils.copy(data, ostream);
      files.put(location, ostream.toByteArray());
    } catch (IOException e) {
      throw new FileStoreException("Unable to store file: " + location, e);
    }
  }

  @Override
  public void deleteFile(String location) throws FileStoreException {
    if (!files.containsKey(location)) {
      LOGGER.warn("File does not exist.");
      return;
    }
    byte[] data = files.remove(location);
    if (data == null) {
      throw new FileStoreException("Unable to delete file: " + location);
    }
  }
}
