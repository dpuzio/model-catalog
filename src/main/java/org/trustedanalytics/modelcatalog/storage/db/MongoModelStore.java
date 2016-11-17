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
package org.trustedanalytics.modelcatalog.storage.db;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.trustedanalytics.modelcatalog.domain.Artifact;
import org.trustedanalytics.modelcatalog.domain.Model;

import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.trustedanalytics.modelcatalog.storage.db.ModelStore;
import org.trustedanalytics.modelcatalog.storage.db.ModelStoreException;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Repository
public class MongoModelStore implements ModelStore {

  private static final String ID = "_id";
  private static final String ARTIFACTS = "artifacts";

  private final MongoOperations mongoOperations;

  @Autowired
  public MongoModelStore(MongoOperations mongoOperations) {
    this.mongoOperations = mongoOperations;
  }

  @Override
  public Collection<Model> listModels(UUID orgId) throws ModelStoreException {
    try {
      return mongoOperations.findAll(Model.class);
    } catch (Exception e) {
      throw new ModelStoreException("Unable to list models.", e);
    }
  }

  @Override
  public Model retrieveModel(UUID modelId) throws ModelStoreException {
    try {
      return mongoOperations.findOne(matchModel(modelId), Model.class);
    } catch (Exception e) {
      throw new ModelStoreException("Unable to retrieve model.", e);
    }
  }

  @Override
  public void addModel(Model model, UUID orgId) throws ModelStoreException {
    try {
      mongoOperations.insert(model);
    } catch (Exception e) {
      throw new ModelStoreException("Unable to add model.", e);
    }
  }

  @Override
  public void updateModel(UUID modelId, Map<String, Object> propertiesToUpdate)
          throws ModelStoreException {
    try {
      Update updateStatement = new Update();
      for (Map.Entry<String, Object> property : propertiesToUpdate.entrySet()) {
        updateStatement.set(property.getKey(), property.getValue());
      }
      WriteResult updateResult = mongoOperations.updateFirst(
              new Query(where(ID).is(modelId)), updateStatement, Model.class);
      verifyWriteResult(updateResult, "No record was updated.");
    } catch (Exception e) {
      throw new ModelStoreException("Unable to update model.", e);
    }
  }

  @Override
  public void deleteModel(UUID modelId) throws ModelStoreException {
    try {
      WriteResult removeResult = mongoOperations.remove(matchModel(modelId), Model.class);
      verifyWriteResult(removeResult, "No model record was removed.");
    } catch (Exception e) {
      throw new ModelStoreException("Unable to delete model.", e);
    }
  }

  @Override
  public void addArtifact(UUID modelId, Artifact artifact) throws ModelStoreException {
    try {
      WriteResult updateResult = mongoOperations.updateFirst(
              matchModel(modelId),
              new Update().addToSet(ARTIFACTS, artifact),
              Model.class);
      verifyWriteResult(updateResult, "No artifact was added.");
    } catch (Exception e) {
      throw new ModelStoreException("Unable to add artifact.", e);
    }
  }

  @Override
  public void deleteArtifact(UUID modelId, UUID artifactId) throws ModelStoreException {
    try {
      WriteResult updateResult = mongoOperations.updateFirst(
              matchModel(modelId),
              new Update().pull(ARTIFACTS, new BasicDBObject(ID, artifactId)),
              Model.class);
      verifyWriteResult(updateResult, "No artifact record was removed.");
    } catch (Exception e) {
      throw new ModelStoreException("Unable to delete artifact.", e);
    }
  }

  private void verifyWriteResult(WriteResult writeResult, String message)
          throws ModelStoreException {
    if (writeResult.getN() <= 0) {
      throw new ModelStoreException(message);
    }
  }

  private Query matchModel(UUID modelId) {
    return new Query(where(ID).is(modelId));
  }

}
