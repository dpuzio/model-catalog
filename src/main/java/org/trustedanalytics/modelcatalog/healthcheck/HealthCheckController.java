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

import org.trustedanalytics.modelcatalog.storage.files.FileStoreException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheckController.class);

  private final HealthCheckMongoTester healthCheckMongoTester;
  private final HealthCheckFileStorageTester healthCheckFileStorageTester;

  @Autowired
  public HealthCheckController(HealthCheckMongoTester healthCheckMongoTester,
                               HealthCheckFileStorageTester healthCheckFileStorageTester) {
    this.healthCheckMongoTester = healthCheckMongoTester;
    this.healthCheckFileStorageTester = healthCheckFileStorageTester;
  }

  @ApiOperation("Checks service health.")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "model-catalog is healthy"),
      @ApiResponse(code = 500, message = "model-catalog is indisposed")
  })
  @RequestMapping(
      value = "/healthz",
      method = RequestMethod.GET
  )
  public void checkHealth() throws FileStoreException{
    healthCheckMongoTester.verifyMongo();
    try {
      healthCheckFileStorageTester.verifyFileStore();
    } catch (FileStoreException e) {
      LOGGER.error("Exception occured when trying to write file: ", e);
      throw e;
    }
  }
}