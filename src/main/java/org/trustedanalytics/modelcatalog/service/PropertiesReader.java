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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class PropertiesReader {

  private static final String IGNORED_CLASS_PROPERTY = "class";

  private PropertiesReader() {}

  public static Map<String, Object> preparePropertiesToUpdateMap(Object source,
      boolean skipNullProperties)
      throws IntrospectionException, InvocationTargetException, IllegalAccessException {
    Map<String, Object> propertiesAndValues = new HashMap<>();
    BeanInfo beanInfo = Introspector.getBeanInfo(source.getClass());
    for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
      addPropertyToMapIfApplicable(property, propertiesAndValues, source, skipNullProperties);
    }
    return propertiesAndValues;
  }

  private static void addPropertyToMapIfApplicable(PropertyDescriptor property,
      Map<String, Object> propertiesAndValues, Object source, boolean skipNullProperties)
      throws IllegalAccessException, InvocationTargetException {
    String propertyName = property.getName();
    if (IGNORED_CLASS_PROPERTY.equals(propertyName)) {
      return;
    }
    Object propertyValue = property.getReadMethod().invoke(source);
    if (skipNullProperties && Objects.isNull(propertyValue)) {
      return;
    }
    propertiesAndValues.put(propertyName, propertyValue);
  }

}
