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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileHelpers {
  
  public void ensureDirectoryExists(Path directory) throws IOException {
    if(!Files.exists(directory)) {
      Files.createDirectories(directory);
    }
  }
  
  public InputStream readFile(Path file) throws IOException {
    return Files.newInputStream(file, StandardOpenOption.READ);
  }
  
  public OutputStream createNewFile(Path file) throws IOException {
    return Files.newOutputStream(file, StandardOpenOption.CREATE_NEW);
  }
  
  public boolean exists(Path path) {
    return Files.exists(path);
  }
  
  public void delete(Path path) throws IOException {
    Files.delete(path);
  }
  
  public void copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
    IOUtils.copy(inputStream, outputStream);
  }
  
  public Path resolvePath(Path basePath, String filePath) {
    Preconditions.checkNotNull(basePath, "Base path cannot be null");
    Preconditions.checkNotNull(filePath, "File path cannot be null");
    String trimmedChildPath = filePath.replaceAll("^[./]+", "");
    Preconditions.checkArgument(!Strings.isNullOrEmpty(trimmedChildPath), "Incorrent file path");
    return basePath.resolve(trimmedChildPath);
  }
}
