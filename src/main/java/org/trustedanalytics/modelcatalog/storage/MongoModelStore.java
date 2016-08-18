/**
 * Copyright (c) 2016 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trustedanalytics.modelcatalog.storage;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.mongodb.WriteResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.trustedanalytics.modelcatalog.domain.Model;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Repository
public class MongoModelStore implements ModelStore {

  private static final String ID = "_id";

  private MongoOperations mongoOperations;

  @Autowired
  public MongoModelStore(MongoOperations mongoOperations) {
    this.mongoOperations = mongoOperations;
  }

  @Override
  public Collection<Model> listModels(UUID orgId) {
    return mongoOperations.findAll(Model.class);
  }

  @Override
  public Model retrieveModel(UUID modelId) {
    return mongoOperations.findOne(matchId(modelId), Model.class);
  }

  @Override
  public OperationStatus addModel(Model model, UUID orgId) {
    mongoOperations.insert(model);
    return OperationStatus.SUCCESS;
  }

  @Override
  public OperationStatus updateModel(UUID modelId, Map<String, Object> propertiesToUpdate) {
    Update updateStatement = new Update();
    for (Map.Entry<String, Object> property : propertiesToUpdate.entrySet()) {
      updateStatement.set(property.getKey(), property.getValue());
    }
    WriteResult updateResult = mongoOperations.updateFirst(
            new Query(where(ID).is(modelId)), updateStatement, Model.class);
    return writeResultToOperationStatus(updateResult);
  }

  @Override
  public OperationStatus deleteModel(UUID modelId) {
    WriteResult removeResult = mongoOperations.remove(matchId(modelId), Model.class);
    return writeResultToOperationStatus(removeResult);
  }

  private OperationStatus writeResultToOperationStatus(WriteResult writeResult) {
    if (writeResult.getN() > 0)
      return OperationStatus.SUCCESS;
    return OperationStatus.FAILURE;
  }

  private Query matchId(UUID modelId) {
    return new Query(where(ID).is(modelId));
  }

}
