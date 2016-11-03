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
package org.trustedanalytics.modelcatalog.rest;

import org.springframework.beans.TypeMismatchException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.trustedanalytics.modelcatalog.service.ModelServiceException;
import org.trustedanalytics.utils.errorhandling.ErrorLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ModelCatalogExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(ModelCatalogExceptionHandler.class);

  @ExceptionHandler(ModelServiceException.class)
  void handleModelServiceException(ModelServiceException e, HttpServletResponse response)
          throws IOException {
    String message = e.getMessage();
    HttpStatus status;
    switch (e.getCode()) {
      case MODEL_NOT_FOUND:
        status = HttpStatus.NOT_FOUND;
        break;
      case MODEL_RETRIEVE_FAILED:
        status = HttpStatus.NOT_FOUND;
        break;
      case ARTIFACT_NOT_FOUND:
        status = HttpStatus.NOT_FOUND;
        break;
      case MODEL_NOTHING_TO_UPDATE:
        status = HttpStatus.NOT_MODIFIED;
        break;
      default:
        status = HttpStatus.INTERNAL_SERVER_ERROR;
        break;
    }

    ErrorLogger.logAndSendErrorResponse(LOGGER, response, status, message, e);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  void handleMissingServletRequestParameterException(MissingServletRequestParameterException e,
                                                     HttpServletResponse response) throws IOException {
    ErrorLogger.logAndSendErrorResponse(LOGGER, response, HttpStatus.BAD_REQUEST, e.getMessage(), e);
  }

  @ExceptionHandler(MissingServletRequestPartException.class)
  void handleMissingServletRequestPartException(MissingServletRequestPartException e,
                                                     HttpServletResponse response) throws IOException {
    ErrorLogger.logAndSendErrorResponse(LOGGER, response, HttpStatus.BAD_REQUEST, e.getMessage(), e);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  void handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e,
                                                    HttpServletResponse response) throws IOException {
    ErrorLogger.logAndSendErrorResponse(LOGGER, response, HttpStatus.NOT_FOUND, e.getMessage(), e);
  }

  @ExceptionHandler(TypeMismatchException.class)
  void handleTypeMismatchException(TypeMismatchException e,
                                   HttpServletResponse response) throws IOException {
    ErrorLogger.logAndSendErrorResponse(LOGGER, response, HttpStatus.BAD_REQUEST, e.getCause().getMessage(), e);
  }
}
