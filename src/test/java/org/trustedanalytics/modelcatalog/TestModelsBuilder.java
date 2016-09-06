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
package org.trustedanalytics.modelcatalog;

import com.google.common.collect.Sets;

import org.trustedanalytics.modelcatalog.domain.Model;

import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

public class TestModelsBuilder {

  public static final UUID ID = UUID.randomUUID();
  public static final String ADDED_BY = "Grzegorz Brzeczyszczykiewicz";
  public static final Date ADDED_ON = new Date();
  public static final String ALGORITHM = "The Algorithm";
  public static final HashSet<UUID> ARTIFACTS_IDS = Sets.newHashSet(UUID.randomUUID(), UUID.randomUUID());
  public static final String DESCRIPTION = "Description";
  public static final String MODIFIED_BY = "ModifiedBy";
  public static final Date MODIFIED_ON = new Date();
  public static final String NAME = "Bored";
  public static final String REVISION = "Revision";

  public static Model prepareExemplaryModel() {
    Model model = new Model();
    model.setId(ID);
    model.setAddedBy(ADDED_BY);
    model.setAddedOn(ADDED_ON);
    model.setAlgorithm(ALGORITHM);
    model.setArtifactsIds(ARTIFACTS_IDS);
    model.setDescription(DESCRIPTION);
    model.setModifiedBy(MODIFIED_BY);
    model.setModifiedOn(MODIFIED_ON);
    model.setName(NAME);
    model.setRevision(REVISION);
    return model;
  }

  public static Model prepareExemplaryModel(UUID modelId) {
    Model model = prepareExemplaryModel();
    model.setId(modelId);
    return model;
  }

}
