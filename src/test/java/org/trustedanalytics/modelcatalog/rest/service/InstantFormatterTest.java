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
package org.trustedanalytics.modelcatalog.rest.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import java.time.Instant;

public class InstantFormatterTest {

  @Test
  public void shouldFormatInstant() {
    // given
    final String instantUTCString = "2016-10-10T13:33:44.55Z";
    final String expectedInstantString = "2016-10-10 13:33:44 GMT";
    Instant instant = Instant.parse(instantUTCString);
    // when
    String formattedInstant = InstantFormatter.format(instant);
    // then
    assertThat(formattedInstant).isEqualTo(expectedInstantString);
  }

  @Test
  public void shouldParseInstant() {
    // given
    final String instantString = "2016-10-10 13:33:44 GMT";
    final String expectedInstantUTCString = "2016-10-10T13:33:44.00Z";
    // when
    Instant parsedInstant = InstantFormatter.parse(instantString);
    // then
    assertThat(parsedInstant).isEqualTo(Instant.parse(expectedInstantUTCString));
  }

}