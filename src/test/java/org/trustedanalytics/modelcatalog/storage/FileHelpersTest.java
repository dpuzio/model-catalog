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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FileHelpersTest {

private FileHelpers fileHelpers;
  
  private static final Path BASE_URL = Paths.get("/mock");
  private static final String SAMPLE_FILE = "/sample/file";
  private static final Path SAMPLE_FILE_PATH = Paths.get(BASE_URL + SAMPLE_FILE);

  @Before
  public void beforeEach() throws IOException {
    fileHelpers = new FileHelpers();
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void resolvePathShouldThrowErrorWhenEmptyLocationGiven() throws FileStoreException, IOException {
    fileHelpers.resolvePath(BASE_URL, "");
  }
  
  @Test(expected = NullPointerException.class)
  public void resolvePathShouldThrowErrorWhenNullLocationGiven() throws FileStoreException, IOException {
    fileHelpers.resolvePath(BASE_URL, null);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void resolvePathShouldThrowErrorWhenDotLocationGiven() throws FileStoreException, IOException {
    fileHelpers.resolvePath(BASE_URL, ".");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void resolvePathShouldThrowErrorWhenParentDirLocationGiven() throws FileStoreException, IOException {
    fileHelpers.resolvePath(BASE_URL, "..");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void resolvePathShouldThrowErrorWhenSlashLocationGiven() throws FileStoreException, IOException {
    fileHelpers.resolvePath(BASE_URL, "/");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void resolvePathShouldThrowErrorWhenSomeParentLocationGiven() throws FileStoreException, IOException {
    fileHelpers.resolvePath(BASE_URL, "../../");
  }
  
  @Test(expected = NullPointerException.class)
  public void resolvePathShouldThrowErrorWhenEmptyBaseDirGiven() throws FileStoreException, IOException {
    fileHelpers.resolvePath(null, "some/path");
  }
  
  @Test
  public void resolvePathShouldResolvePathCorrectly() throws FileStoreException, IOException {
    Path result = fileHelpers.resolvePath(BASE_URL, "some/path");
    assertEquals(Paths.get("/mock/some/path"), result);
    
    result = fileHelpers.resolvePath(BASE_URL, "/some/path");
    assertEquals(Paths.get("/mock/some/path"), result);
    
    result = fileHelpers.resolvePath(BASE_URL, "../../some/path");
    assertEquals(Paths.get("/mock/some/path"), result);
    
    result = fileHelpers.resolvePath(BASE_URL, "//some/path");
    assertEquals(Paths.get("/mock/some/path"), result);
  }
}