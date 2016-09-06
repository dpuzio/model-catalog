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
package org.trustedanalytics.modelcatalog.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.trustedanalytics.modelcatalog.TestModelsBuilder;
import org.trustedanalytics.modelcatalog.domain.Model;

import java.lang.reflect.Field;
import java.util.Map;

public class PropertiesReaderTest {

  @Test
  public void shouldPreparePropertiesMap() throws Exception {
    // given
    Model model = TestModelsBuilder.prepareExemplaryModel();
    // when
    Map<String, Object> properties = PropertiesReader.preparePropertiesToUpdateMap(model, true);
    // then
    checkThatMapContainsModelProperties(properties);
  }

  @Test
  public void shouldSkipClassProperty() throws Exception {
    // given
    Model model = new Model();
    // when
    Map<String, Object> properties = PropertiesReader.preparePropertiesToUpdateMap(model, false);
    // then
    assertThat(properties.keySet()).doesNotContain("class");
  }

  @Test
  public void shouldSkipIdProperty() throws Exception {
    // given
    Model model = new Model();
    // when
    Map<String, Object> properties = PropertiesReader.preparePropertiesToUpdateMap(model, false);
    // then
    assertThat(properties.keySet()).doesNotContain("id");
  }


  @Test
  public void shouldMapNullProperties_whenSkipNullPropertiesIsFalse() throws Exception {
    // given
    Model model = new Model();
    // when
    Map<String, Object> properties = PropertiesReader.preparePropertiesToUpdateMap(model, false);
    // then
    Field[] fields = Model.class.getDeclaredFields();
    for (Field field : fields) {
      if (field.getName().equals("DATE_FORMAT")) continue; //it's not a bean property
      if (field.getName().equals("id")) continue; //shouldn't map id
      if (field.getName().equals("$jacocoData")) continue; //yep
      assertThat(properties.keySet()).contains(field.getName());
      assertThat(properties.get(field)).isNull();
    }
  }

  @Test
  public void shouldSkipNullProperties_whenSkipNullPropertiesIsTrue() throws Exception {
    // given
    Model model = new Model();
    // when
    Map<String, Object> properties = PropertiesReader.preparePropertiesToUpdateMap(model, true);
    // then
    assertThat(properties).isEmpty();
  }

  private void checkThatMapContainsModelProperties(Map<String, Object> properties) {
    assertThat(properties.get("addedBy")).isEqualTo(TestModelsBuilder.ADDED_BY);
    assertThat(properties.get("addedOn")).isEqualTo(TestModelsBuilder.ADDED_ON);
    assertThat(properties.get("algorithm")).isEqualTo(TestModelsBuilder.ALGORITHM);
    assertThat(properties.get("artifactsIds")).isEqualTo(TestModelsBuilder.ARTIFACTS_IDS);
    assertThat(properties.get("description")).isEqualTo(TestModelsBuilder.DESCRIPTION);
    assertThat(properties.get("modifiedBy")).isEqualTo(TestModelsBuilder.MODIFIED_BY);
    assertThat(properties.get("modifiedOn")).isEqualTo(TestModelsBuilder.MODIFIED_ON);
    assertThat(properties.get("name")).isEqualTo(TestModelsBuilder.NAME);
    assertThat(properties.get("revision")).isEqualTo(TestModelsBuilder.REVISION);
  }

}