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
package org.trustedanalytics.modelcatalog.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.trustedanalytics.modelcatalog.rest.entities.ModelDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ModelModificationParametersDTO;
import org.trustedanalytics.modelcatalog.rest.service.RestService;
import org.trustedanalytics.modelcatalog.service.CannotMapPropertiesException;
import org.trustedanalytics.modelcatalog.service.FailedUpdateException;
import org.trustedanalytics.modelcatalog.service.ModelNotFoundException;
import org.trustedanalytics.modelcatalog.service.NothingToUpdateException;

import java.net.URI;
import java.util.Collection;
import java.util.UUID;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class Controller {

  private final RestService service;

  @Autowired
  public Controller(RestService service) {
    this.service = service;
  }

  @ApiOperation(
          value = "Get all models in given organization.",
          notes = "Privilege level: Consumer of this endpoint must have a valid access token"
  )
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "SUCCESS"),
          @ApiResponse(code = 500, message = "Internal server error, e.g. error getting model metadata")
  })
  @RequestMapping(
          value = ModelCatalogPaths.MODELS,
          method = RequestMethod.GET,
          produces = "application/json; charset=UTF-8"
  )
  public Collection<ModelDTO> listModels(
          @ApiParam(value = "Organization id", required = true) @RequestParam UUID orgId) {
    return service.listModels(orgId);
  }

  @ApiOperation(
          value = "Gets the model with specified id",
          notes = "Privilege level: Consumer of this endpoint must have a valid access token"
  )
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "SUCCESS"),
          @ApiResponse(code = 404, message = "Not Found"),
          @ApiResponse(code = 500, message = "Internal server error, e.g. error getting model metadata"),
  })
  @RequestMapping(
          value = ModelCatalogPaths.MODEL,
          method = RequestMethod.GET,
          produces = "application/json; charset=UTF-8"
  )
  public ModelDTO retrieveModel(
          @ApiParam(value = "Model id", required = true) @PathVariable UUID modelId) {
    return service.retrieveModel(modelId);
  }

  @ApiOperation(
          value = "Inserts new model entity with given parameters in given organization.",
          notes = "Privilege level: Consumer of this endpoint must have a valid access token"
  )
  @ApiResponses(value = {
          @ApiResponse(code = 201, message = "Created"),
          @ApiResponse(code = 500, message = "Internal server error, e.g. error saving model metadata"),
  })
  @RequestMapping(
          value = ModelCatalogPaths.MODELS,
          method = RequestMethod.POST,
          produces = "application/json; charset=UTF-8")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<ModelDTO> addModelAndReturnWithLocationHeader (
          @ApiParam(value = "Model entity containing only modifiable fields", required = true) @RequestBody ModelModificationParametersDTO model,
          @ApiParam(value = "Organization id", required = true) @RequestParam UUID orgId) {
    ModelDTO addedModel = service.addModel(model, orgId);
    HttpHeaders httpHeaders = new HttpHeaders();
    addModelLocation(addedModel, httpHeaders);
    return new ResponseEntity<>(addedModel, httpHeaders, HttpStatus.CREATED);
  }

  @ApiOperation(
          value = "Updates model entity",
          notes = "Privilege level: Consumer of this endpoint must have a valid access token"
  )
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Model updated"),
          @ApiResponse(code = 404, message = "Model not Found"),
          @ApiResponse(code = 500, message = "Internal server error, e.g. error updating model metadata"),
  })
  @RequestMapping(
          value = ModelCatalogPaths.MODEL,
          method = RequestMethod.PUT,
          produces = "application/json; charset=UTF-8")
  public ModelDTO updateModel(
          @ApiParam(value = "Model id", required = true) @PathVariable UUID modelId,
          @ApiParam(value = "Model entity containing only modifiable fields", required = true) @RequestBody ModelModificationParametersDTO model) {
    return service.updateModel(modelId, model);
  }

  @ApiOperation(
          value = "Updates given fields of a model entity",
          notes = "Privilege level: Consumer of this endpoint must have a valid access token"
  )
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Model updated"),
          @ApiResponse(code = 304, message = "Nothing to update"),
          @ApiResponse(code = 404, message = "Model not Found"),
          @ApiResponse(code = 500, message = "Internal server error, e.g. error updating model metadata"),
  })
  @RequestMapping(
          value = ModelCatalogPaths.MODEL,
          method = RequestMethod.PATCH,
          produces = "application/json; charset=UTF-8")
  public ModelDTO patchModel(
          @ApiParam(value = "Model id", required = true) @PathVariable UUID modelId,
          @ApiParam(value = "Model entity containing only modifiable fields", required = true) @RequestBody ModelModificationParametersDTO model) {
    return service.patchModel(modelId, model);
  }

  @ApiOperation(
          value = "Deletes model entity with given id",
          notes = "Privilege level: Consumer of this endpoint must have a valid access token"
  )
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Deleted"),
          @ApiResponse(code = 404, message = "Model not Found"),
          @ApiResponse(code = 500, message = "Internal server error, e.g. error saving model metadata"),
  })
  @RequestMapping(
          value = ModelCatalogPaths.MODEL,
          method = RequestMethod.DELETE,
          produces = "application/json; charset=UTF-8")
  public ModelDTO deleteModel(
          @ApiParam(value = "Model id", required = true) @PathVariable UUID modelId) {
    return service.deleteModel(modelId);
  }

  @SuppressWarnings("EmptyMethod")
  @ExceptionHandler(CannotMapPropertiesException.class)
  @ResponseStatus(
          value = HttpStatus.INTERNAL_SERVER_ERROR,
          reason = "Cannot process entity properties.")
  void handleCannotMapPropertiesException() {
  }

  @SuppressWarnings("EmptyMethod")
  @ExceptionHandler(FailedUpdateException.class)
  @ResponseStatus(
          value = HttpStatus.INTERNAL_SERVER_ERROR,
          reason = "Database update failed.")
  void handleFailedUpdateException() {
  }

  @SuppressWarnings("EmptyMethod")
  @ExceptionHandler(ModelNotFoundException.class)
  @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Model with given id not found")
  void handleModelNotFoundException() {
  }

  @SuppressWarnings("EmptyMethod")
  @ExceptionHandler(NothingToUpdateException.class)
  @ResponseStatus(value= HttpStatus.NOT_MODIFIED, reason = "Nothing to update")
  void handleNothingToUpdateException() {
  }

  private void addModelLocation(ModelDTO addedModel, HttpHeaders httpHeaders) {
    String locationString = ModelCatalogPaths.pathToSpecificModel(addedModel.getId());
    URI location = URI.create(locationString);
    httpHeaders.setLocation(location);
  }

}
