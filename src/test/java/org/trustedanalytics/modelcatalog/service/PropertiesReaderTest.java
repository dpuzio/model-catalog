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
package org.trustedanalytics.modelcatalog.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.trustedanalytics.modelcatalog.TestModelParamsBuilder;

import java.lang.reflect.Field;
import java.util.Map;

public class PropertiesReaderTest {

  @Test
  public void shouldPreparePropertiesMap() throws Exception {
    // given
    ModelModificationParameters params = TestModelParamsBuilder.exemplaryParams();
    // when
    Map<String, Object> properties = PropertiesReader.preparePropertiesToUpdateMap(params, true);
    // then
    checkThatMapContainsAllParams(properties);
  }

  @Test
  public void shouldSkipClassProperty() throws Exception {
    // given
    ModelModificationParameters params = TestModelParamsBuilder.exemplaryParams();
    // when
    Map<String, Object> properties = PropertiesReader.preparePropertiesToUpdateMap(params, false);
    // then
    assertThat(properties.keySet()).doesNotContain("class");
  }

  @Test
  public void shouldMapNullProperties_whenSkipNullPropertiesIsFalse() throws Exception {
    // given
    ModelModificationParameters params = TestModelParamsBuilder.emptyParams();
    // when
    Map<String, Object> properties = PropertiesReader.preparePropertiesToUpdateMap(params, false);
    // then
    Field[] fields = ModelModificationParameters.class.getDeclaredFields();
    for (Field field : fields) {
      if (field.getName().equals("$jacocoData")) {
        continue; //yep
      }
      assertThat(properties.keySet()).contains(field.getName());
      assertThat(properties.get(field)).isNull();
    }
  }

  @Test
  public void shouldSkipNullProperties_whenSkipNullPropertiesIsTrue() throws Exception {
    // given
    ModelModificationParameters params = TestModelParamsBuilder.emptyParams();
    // when
    Map<String, Object> properties = PropertiesReader.preparePropertiesToUpdateMap(params, true);
    // then
    assertThat(properties).isEmpty();
  }

  private void checkThatMapContainsAllParams(Map<String, Object> properties) {
    assertThat(properties.get("name")).isEqualTo(TestModelParamsBuilder.NAME);
    assertThat(properties.get("revision")).isEqualTo(TestModelParamsBuilder.REVISION);
    assertThat(properties.get("algorithm")).isEqualTo(TestModelParamsBuilder.ALGORITHM);
    assertThat(properties.get("creationTool")).isEqualTo(TestModelParamsBuilder.CREATION_TOOL);
    assertThat(properties.get("description")).isEqualTo(TestModelParamsBuilder.DESCRIPTION);
    assertThat(properties.get("artifactsIds")).isEqualTo(TestModelParamsBuilder.ARTIFACTS_IDS);
  }

}