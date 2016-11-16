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

import org.trustedanalytics.modelcatalog.rest.entities.ModelDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ModelModificationParametersDTO;
import org.trustedanalytics.modelcatalog.rest.service.ModelsRestService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Collection;
import java.util.UUID;

@RestController
public class ModelsController {

  private final ModelsRestService service;

  @Autowired
  public ModelsController(ModelsRestService service) {
    this.service = service;
  }

  @ApiOperation(
          value = "Returns all models in given organization.",
          notes = "Privilege level: Consumer of this endpoint must have a valid access token"
  )
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "SUCCESS"),
          @ApiResponse(code = 500, message = "Internal server error, e.g. error getting model " +
                  "metadata")
  })
  @RequestMapping(
          value = ModelCatalogPaths.MODELS,
          method = RequestMethod.GET,
          produces = RequestParams.CONTENT_TYPE_APP_JSON_UTF
  )
  public Collection<ModelDTO> listModels(
          @ApiParam(value = "Organization id", required = true) @RequestParam UUID orgId) {
    return service.listModels(orgId);
  }

  @ApiOperation(
          value = "Returns model metadata",
          notes = "Privilege level: Consumer of this endpoint must have a valid access token"
  )
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "SUCCESS"),
          @ApiResponse(code = 404, message = "Not Found"),
          @ApiResponse(code = 500, message = "Internal server error, e.g. error getting model " +
                  "metadata"),
  })
  @RequestMapping(
          value = ModelCatalogPaths.MODEL,
          method = RequestMethod.GET,
          produces = RequestParams.CONTENT_TYPE_APP_JSON_UTF
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
          @ApiResponse(code = 400, message = "Bad request, e.g. missing required model parameters"),
          @ApiResponse(code = 500, message = "Internal server error, e.g. error saving model " +
                  "metadata"),
  })
  @RequestMapping(
          value = ModelCatalogPaths.MODELS,
          method = RequestMethod.POST,
          produces = RequestParams.CONTENT_TYPE_APP_JSON_UTF)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<ModelDTO> addModelAndReturnWithLocationHeader(
          @ApiParam(value = "Model entity containing only modifiable fields", required = true)
          @RequestBody ModelModificationParametersDTO model,
          @ApiParam(value = "Organization id", required = true) @RequestParam UUID orgId) {
    ModelDTO addedModel = service.addModel(model, orgId);
    HttpHeaders httpHeaders = new HttpHeaders();
    addModelLocation(addedModel, httpHeaders);
    return new ResponseEntity<>(addedModel, httpHeaders, HttpStatus.CREATED);
  }

  @ApiOperation(
          value = "Updates model",
          notes = "Privilege level: Consumer of this endpoint must have a valid access token"
  )
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Model updated"),
          @ApiResponse(code = 400, message = "Bad request, e.g. missing required model parameters"),
          @ApiResponse(code = 404, message = "Model not Found"),
          @ApiResponse(code = 500, message = "Internal server error, e.g. error updating model " +
                  "metadata"),
  })
  @RequestMapping(
          value = ModelCatalogPaths.MODEL,
          method = RequestMethod.PUT,
          produces = RequestParams.CONTENT_TYPE_APP_JSON_UTF)
  public ModelDTO updateModel(
          @ApiParam(value = "Model id", required = true) @PathVariable UUID modelId,
          @ApiParam(value = "Model entity containing only modifiable fields", required = true)
          @RequestBody ModelModificationParametersDTO model) {
    return service.updateModel(modelId, model);
  }

  @ApiOperation(
          value = "Updates given model properties",
          notes = "Privilege level: Consumer of this endpoint must have a valid access token"
  )
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Model updated"),
          @ApiResponse(code = 304, message = "Nothing to update"),
          @ApiResponse(code = 404, message = "Model not Found"),
          @ApiResponse(code = 500, message = "Internal server error, e.g. error updating model " +
                  "metadata"),
  })
  @RequestMapping(
          value = ModelCatalogPaths.MODEL,
          method = RequestMethod.PATCH,
          produces = RequestParams.CONTENT_TYPE_APP_JSON_UTF)
  public ModelDTO patchModel(
          @ApiParam(value = "Model id", required = true) @PathVariable UUID modelId,
          @ApiParam(value = "Model entity containing only modifiable fields", required = true)
          @RequestBody ModelModificationParametersDTO model) {
    return service.patchModel(modelId, model);
  }

  @ApiOperation(
          value = "Deletes model",
          notes = "Privilege level: Consumer of this endpoint must have a valid access token"
  )
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Deleted"),
          @ApiResponse(code = 404, message = "Model not Found"),
          @ApiResponse(code = 500, message = "Internal server error, e.g. error saving model " +
                  "metadata"),
  })
  @RequestMapping(
          value = ModelCatalogPaths.MODEL,
          method = RequestMethod.DELETE,
          produces = RequestParams.CONTENT_TYPE_APP_JSON_UTF)
  public ModelDTO deleteModel(
          @ApiParam(value = "Model id", required = true) @PathVariable UUID modelId) {
    return service.deleteModel(modelId);
  }

  private void addModelLocation(ModelDTO addedModel, HttpHeaders httpHeaders) {
    String locationString = ModelCatalogPaths.pathToModel(addedModel.getId());
    URI location = URI.create(locationString);
    httpHeaders.setLocation(location);
  }

}
