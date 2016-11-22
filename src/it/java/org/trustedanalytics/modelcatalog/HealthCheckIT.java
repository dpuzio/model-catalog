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

import static org.assertj.core.api.Assertions.assertThat;
import static org.trustedanalytics.modelcatalog.ExpectedExceptionsHelper.expectHttpServerErrorException;

import org.trustedanalytics.modelcatalog.storage.files.FileStoreException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebAppConfiguration
@IntegrationTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles({"integration-test", "in-memory", "health-check"})
public class HealthCheckIT {

  @Value("http://localhost:${local.server.port}/healthz")
  private String healthCheckPath;
  private RestTemplate restTemplate;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void init() {
    restTemplate = new RestTemplate();
  }

  @Test
  public void healthCheckEndpoint_shouldBeInsecureAndReturn200_whenAllOk() {
    ResponseEntity<String> response = restTemplate.getForEntity(healthCheckPath, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void healthCheckEndpoint_shouldReturn500_whenMongoDoesNotWork() {
    HealthCheckConfig.throwErrorWhenTalkingToMongo();
    expectHttpServerErrorException(thrown, HttpStatus.INTERNAL_SERVER_ERROR);
    restTemplate.getForEntity(healthCheckPath, String.class);
  }

  @Test
  public void healthCheckEndpoint_shouldReturn500_whenLocalStorageDoesNotWork() throws FileStoreException {
    HealthCheckConfig.throwErrorWhenTalkingToFileStorage();
    expectHttpServerErrorException(thrown, HttpStatus.INTERNAL_SERVER_ERROR);
    restTemplate.getForEntity(healthCheckPath, String.class);

  }
}
