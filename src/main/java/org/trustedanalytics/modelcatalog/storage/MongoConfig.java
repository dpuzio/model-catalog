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

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {

  @Autowired
  private MongoProperties mongoProperties;

  @Override
  protected String getDatabaseName() {
    return mongoProperties.getDbName();
  }

  @Bean
  @Override
  public Mongo mongo() throws UnknownHostException {
    ServerAddress serverAddress = new ServerAddress(mongoProperties.getServerName(), mongoProperties.getServerPort());
    List<MongoCredential> credendialList = new ArrayList<>();

    String user = mongoProperties.getUser();
    if (user != null && !user.isEmpty()) {
      credendialList.add(MongoCredential.createMongoCRCredential(
              user, mongoProperties.getDbName(), mongoProperties.getPassword().toCharArray()));
    }

    return new MongoClient(serverAddress, credendialList);
  }

}
