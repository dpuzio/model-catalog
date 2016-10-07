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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LocalFileStoreTest {

  @Mock
  private FileHelpers fileHelpers;
  
  private static final Path BASE_URL = Paths.get("/base/path/mock");
  private static final String SAMPLE_FILE = "/sample/file";
  private static final Path SAMPLE_FILE_PATH = Paths.get(BASE_URL + SAMPLE_FILE);

  private LocalFileStore localFileStore;
  
  @Before
  public void beforeEach() throws IOException {
    localFileStore = new LocalFileStore(BASE_URL, fileHelpers);
    when(fileHelpers.resolvePath(BASE_URL, SAMPLE_FILE)).thenReturn(SAMPLE_FILE_PATH);
  }
  
  @Test
  public void shouldEnsureBaseDirExist() throws IOException {
    verify(fileHelpers).ensureDirectoryExists(BASE_URL);
  }
  
  @Test(expected = FileStoreException.class)
  public void retrieveFileShouldThrowErrorWhenFileDoesNotExist() throws FileStoreException {
    when(fileHelpers.exists(any(Path.class))).thenReturn(false);
    
    localFileStore.retrieveFile(SAMPLE_FILE);
  }
  
  @Test(expected = FileStoreException.class)
  public void retrieveFileShouldThrowErrorWhenFileReadingFails() throws FileStoreException, IOException {
    when(fileHelpers.exists(SAMPLE_FILE_PATH)).thenReturn(true);
    when(fileHelpers.readFile(SAMPLE_FILE_PATH)).thenThrow(new IOException());
    
    localFileStore.retrieveFile(SAMPLE_FILE);
  }
  
  @Test
  public void retrieveFileShouldReturnInputStream() throws FileStoreException, IOException {
    when(fileHelpers.exists(SAMPLE_FILE_PATH)).thenReturn(true);
    InputStream inputStream = mock(InputStream.class);
    when(fileHelpers.readFile(SAMPLE_FILE_PATH)).thenReturn(inputStream);
    
    InputStream result = localFileStore.retrieveFile(SAMPLE_FILE);
    
    assertEquals(inputStream, result);
  }
  
  @Test
  public void addFileShouldWriteStreamToAFile() throws IOException, FileStoreException {
    InputStream inputStream = mock(InputStream.class);
    OutputStream outputStream = mock(OutputStream.class);
    when(fileHelpers.createNewFile(SAMPLE_FILE_PATH)).thenReturn(outputStream);
    
    localFileStore.addFile(SAMPLE_FILE, inputStream);
    
    verify(fileHelpers).ensureDirectoryExists(Paths.get(BASE_URL + "/sample"));
    verify(fileHelpers).createNewFile(SAMPLE_FILE_PATH);
    verify(fileHelpers).copyStream(inputStream, outputStream);
  }
  
  @Test(expected = FileStoreException.class)
  public void addFileShouldThrowExceptionWhenCreatingDirFails() throws IOException, FileStoreException {
    InputStream inputStream = mock(InputStream.class);
    doThrow(new IOException()).when(fileHelpers).ensureDirectoryExists(any(Path.class));
    
    localFileStore.addFile(SAMPLE_FILE, inputStream);

    verify(fileHelpers).ensureDirectoryExists(Paths.get(BASE_URL + "/sample"));
    verify(fileHelpers, never()).createNewFile(any(Path.class));
    verify(fileHelpers, never()).copyStream(any(InputStream.class), any(OutputStream.class));
  }
  
  @Test(expected = FileStoreException.class)
  public void addFileShouldThrowExceptionWhenCreatingFileFails() throws IOException, FileStoreException {
    InputStream inputStream = mock(InputStream.class);
    when(fileHelpers.createNewFile(SAMPLE_FILE_PATH)).thenThrow(new IOException());
    
    localFileStore.addFile(SAMPLE_FILE, inputStream);
    
    verify(fileHelpers).ensureDirectoryExists(Paths.get(BASE_URL + "/sample"));
    verify(fileHelpers).createNewFile(SAMPLE_FILE_PATH);
    verify(fileHelpers, never()).copyStream(any(InputStream.class), any(OutputStream.class));
  }
  
  @Test(expected = FileStoreException.class)
  public void addFileShouldThrowExceptionWhenCopyingStreamFails() throws IOException, FileStoreException {
    InputStream inputStream = mock(InputStream.class);
    OutputStream outputStream = mock(OutputStream.class);
    when(fileHelpers.createNewFile(SAMPLE_FILE_PATH)).thenReturn(outputStream);
    doThrow(new IOException()).when(fileHelpers).copyStream(any(InputStream.class), any(OutputStream.class));
    
    localFileStore.addFile(SAMPLE_FILE, inputStream);
    
    verify(fileHelpers).ensureDirectoryExists(Paths.get(BASE_URL + "/sample"));
    verify(fileHelpers).createNewFile(SAMPLE_FILE_PATH);
    verify(fileHelpers).copyStream(inputStream, outputStream);
  }
  
  @Test
  public void deleteFileShouldNotDeleteIfFileDoesNotExist() throws FileStoreException, IOException {
    when(fileHelpers.exists(SAMPLE_FILE_PATH)).thenReturn(false);
    
    localFileStore.deleteFile(SAMPLE_FILE);
    
    verify(fileHelpers, never()).delete(SAMPLE_FILE_PATH);
  }
  
  @Test(expected = FileStoreException.class)
  public void deleteFileShouldThrowExceptionWhenFileDeletionFailed() throws FileStoreException, IOException {
    when(fileHelpers.exists(SAMPLE_FILE_PATH)).thenReturn(true);
    doThrow(new IOException()).when(fileHelpers).delete(SAMPLE_FILE_PATH);
    
    localFileStore.deleteFile(SAMPLE_FILE);
  }
  
  @Test
  public void deleteFileShouldDeleteFileIfExists() throws FileStoreException, IOException {
    when(fileHelpers.exists(SAMPLE_FILE_PATH)).thenReturn(true);
    
    localFileStore.deleteFile(SAMPLE_FILE);
    
    verify(fileHelpers).delete(SAMPLE_FILE_PATH);
  }
}