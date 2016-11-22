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
package org.trustedanalytics.modelcatalog;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.hamcrest.CoreMatchers.containsString;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.trustedanalytics.modelcatalog.rest.client.ModelCatalogClientException;

import org.assertj.core.api.ThrowableAssert;
import org.junit.rules.ExpectedException;
import org.springframework.http.HttpStatus;

class ExpectedExceptionsHelper {

  static void expectModelCatalogExceptionWithStatusAndReason(
          ExpectedException thrown, HttpStatus status) {
    expectModelCatalogExceptionWithStatus(thrown, status);
    thrown.expectMessage(containsString(status.getReasonPhrase()));
  }

  static void expectModelCatalogExceptionWithStatus(
          ExpectedException thrown, HttpStatus status) {
    thrown.expect(ModelCatalogClientException.class);
    thrown.expectMessage(containsString(status.toString()));
  }

  static void expectNotFoundExceptionThrownBy(ThrowableAssert.ThrowingCallable throwingCallable) {
    assertThatExceptionOfType(ModelCatalogClientException.class)
            .isThrownBy(throwingCallable)
            .withMessageContaining(HttpStatus.NOT_FOUND.toString());
  }

  static void expectHttpClientErrorException(ExpectedException thrown, HttpStatus status) {
    thrown.expect(HttpClientErrorException.class);
    thrown.expectMessage(containsString(status.toString()));
  }

  static void expectHttpServerErrorException(ExpectedException thrown, HttpStatus status) {
    thrown.expect(HttpServerErrorException.class);
    thrown.expectMessage(containsString(status.toString()));
  }
}
