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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.trustedanalytics.modelcatalog.healthcheck.HealthCheckTestObject;
import org.trustedanalytics.modelcatalog.storage.files.FileStore;
import org.trustedanalytics.modelcatalog.storage.files.FileStoreException;
import org.trustedanalytics.modelcatalog.storage.files.MemoryFileStore;

import org.mockito.Mockito;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;

import java.io.InputStream;

@Configuration
@Profile("health-check")
public class HealthCheckConfig {

  private static final MongoOperations mongoOperations = mock(MongoOperations.class);
  private static final MemoryFileStore fileStore = mock(MemoryFileStore.class);

  @Bean
  public MongoOperations mongoOperations() {
    Mockito.reset(mongoOperations);
    return mongoOperations;
  }

  @Bean
  FileStore fileStore() {
    Mockito.reset(fileStore);
    return fileStore;
  }

  public static void throwErrorWhenTalkingToMongo() {
    doThrow(new Error()).when(mongoOperations).insert(any(HealthCheckTestObject.class));
  }

  public static void throwErrorWhenTalkingToFileStorage() throws FileStoreException {
    doThrow(new Error()).when(fileStore).addFile(anyString(), any(InputStream.class));
  }
}
