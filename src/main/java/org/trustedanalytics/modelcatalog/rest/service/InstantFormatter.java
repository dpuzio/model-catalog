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
package org.trustedanalytics.modelcatalog.rest.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class InstantFormatter {

  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm z";
  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern(DATE_FORMAT).withZone(ZoneId.of("GMT"));

  private InstantFormatter() {}

  public static String format(Instant instant) {
    if (null == instant) {
      return null;
    }
    return formatter.format(instant);
  }

  public static Instant parse(String txt) {
    return Instant.from(formatter.parse(txt));
  }

}
