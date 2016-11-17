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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class HealthCheckMongoTester {

  private static final String ID = "_id";
  private final MongoOperations mongoOperations;

  @Autowired
  public HealthCheckMongoTester(MongoOperations mongoOperations) {
    this.mongoOperations = mongoOperations;
  }

  public void verifyMongo() {
    HealthCheckTestObject testObject = addHealthCheckTestObject(new HealthCheckTestObject());
    deleteHealthCheckTestObject(testObject.getId());
  }

  private HealthCheckTestObject addHealthCheckTestObject(HealthCheckTestObject healthCheckTestObject) {
    mongoOperations.insert(healthCheckTestObject);
    return healthCheckTestObject;
  }

  private void deleteHealthCheckTestObject(String id) {
    mongoOperations.remove(matchHealthCheckTestObject(id), HealthCheckTestObject.class);
  }

  private Query matchHealthCheckTestObject(String objectId) {
    return new Query().addCriteria(Criteria.where(ID).is(objectId));
  }
}