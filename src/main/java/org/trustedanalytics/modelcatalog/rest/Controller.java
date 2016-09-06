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
import org.trustedanalytics.modelcatalog.service.CannotMapPropertiesException;
import org.trustedanalytics.modelcatalog.service.FailedUpdateException;
import org.trustedanalytics.modelcatalog.service.MismatchedIdsException;
import org.trustedanalytics.modelcatalog.service.ModelNotFoundException;
import org.trustedanalytics.modelcatalog.service.NothingToUpdateException;
import org.trustedanalytics.modelcatalog.domain.Model;
import org.trustedanalytics.modelcatalog.service.ModelService;

import java.net.URI;
import java.util.Collection;
import java.util.UUID;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class Controller {

  private ModelService modelService;

  @Autowired
  public Controller(ModelService modelService) {
    this.modelService = modelService;
  }

  @ApiOperation(
          value = "Get all models in given organization."
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
  public Collection<Model> listModels(
          @ApiParam(value = "Organization id", required = true) @RequestParam UUID orgId) {
    return modelService.listModels(orgId);
  }

  @ApiOperation(
          value = "Gets the model with specified id"
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
  public Model retrieveModel(
          @ApiParam(value = "Model id", required = true) @PathVariable UUID modelId) {
    return modelService.retrieveModel(modelId);
  }

  @ApiOperation(
          value = "Inserts given model entity in given organization",
          notes = "Model gets random UUID - returned in the response in Model entity."
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
  public ResponseEntity<Model> addModelAndReturnWithLocationHeader (
          @ApiParam(value = "Model entity", required = true) @RequestBody Model model,
          @ApiParam(value = "Organization id", required = true) @RequestParam UUID orgId) {
    Model addedModel = modelService.addModel(model, orgId);
    HttpHeaders httpHeaders = new HttpHeaders();
    addModelLocation(addedModel, httpHeaders);
    return new ResponseEntity<>(addedModel, httpHeaders, HttpStatus.CREATED);
  }

  @ApiOperation(
          value = "Updates model entity"
  )
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Model updated"),
          @ApiResponse(code = 404, message = "Model not Found"),
          @ApiResponse(code = 422, message = "Model entity contains id that differs from the one in URI"),
          @ApiResponse(code = 500, message = "Internal server error, e.g. error updating model metadata"),
  })
  @RequestMapping(
          value = ModelCatalogPaths.MODEL,
          method = RequestMethod.PUT,
          produces = "application/json; charset=UTF-8")
  public Model updateModel(
          @ApiParam(value = "Model id", required = true) @PathVariable UUID modelId,
          @ApiParam(value = "Model entity", required = true) @RequestBody Model model) {
    return modelService.updateModel(modelId, model);
  }

  @ApiOperation(
          value = "Updates given fields of a model entity"
  )
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Model updated"),
          @ApiResponse(code = 304, message = "Nothing to update"),
          @ApiResponse(code = 404, message = "Model not Found"),
          @ApiResponse(code = 422, message = "Model entity contains id that differs from the one in URI"),
          @ApiResponse(code = 500, message = "Internal server error, e.g. error updating model metadata"),
  })
  @RequestMapping(
          value = ModelCatalogPaths.MODEL,
          method = RequestMethod.PATCH,
          produces = "application/json; charset=UTF-8")
  public Model patchModel(
          @ApiParam(value = "Model id", required = true) @PathVariable UUID modelId,
          @ApiParam(value = "Model entity", required = true) @RequestBody Model model) {
    return modelService.patchModel(modelId, model);
  }

  @ApiOperation(
          value = "Deletes model entity with given id"
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
  public Model deleteModel(
          @ApiParam(value = "Model id", required = true) @PathVariable UUID modelId) {
    return modelService.deleteModel(modelId);
  }

  @ExceptionHandler(CannotMapPropertiesException.class)
  @ResponseStatus(
          value = HttpStatus.INTERNAL_SERVER_ERROR,
          reason = "Cannot process entity properties.")
  void handleCannotMapPropertiesException() {
  }

  @ExceptionHandler(FailedUpdateException.class)
  @ResponseStatus(
          value = HttpStatus.INTERNAL_SERVER_ERROR,
          reason = "Database update failed.")
  void handleFailedUpdateException() {
  }

  @ExceptionHandler(MismatchedIdsException.class)
  @ResponseStatus(
          value = HttpStatus.UNPROCESSABLE_ENTITY,
          reason = "Entity id in the URI does not match the one inside the entity.")
  void handleMismatchedIdsException() {
  }

  @ExceptionHandler(ModelNotFoundException.class)
  @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Model with given id not found")
  void handleModelNotFoundException() {
  }

  @ExceptionHandler(NothingToUpdateException.class)
  @ResponseStatus(value= HttpStatus.NOT_MODIFIED, reason = "Nothing to update")
  void handleNothingToUpdateException() {
  }

  private void addModelLocation(Model addedModel, HttpHeaders httpHeaders) {
    String locationString = ModelCatalogPaths.pathToSpecificModel(addedModel.getId());
    URI location = URI.create(locationString);
    httpHeaders.setLocation(location);
  }

}
