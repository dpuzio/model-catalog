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

import org.trustedanalytics.modelcatalog.rest.entities.ArtifactDTO;
import org.trustedanalytics.modelcatalog.rest.service.ArtifactsRestService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;

@RestController
public class ArtifactsController {

  private final ArtifactsRestService service;

  @Autowired
  public ArtifactsController(ArtifactsRestService service) {
    this.service = service;
  }

  @ApiOperation(
          value = "Uploads model artifact metadata and file",
          notes = "Privilege level: Consumer of this endpoint must have a valid access token"
  )
  @ApiResponses(value = {
          @ApiResponse(code = 201, message = "Created"),
          @ApiResponse(code = 404, message = "Model not found"),
          @ApiResponse(code = 500, message =
                  "Internal server error, e.g. error saving artifact file"),
  })
  @RequestMapping(
          value = ModelCatalogPaths.ARTIFACTS,
          method = RequestMethod.POST,
          consumes = "multipart/*",
          produces = RequestParams.CONTENT_TYPE_APP_JSON_UTF)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<ArtifactDTO> addArtifactAndReturnWithLocationHeader(
          @ApiParam(value = "Model id", required = true) @PathVariable UUID modelId,
          @ApiParam(value = "Artifact actions", required = false)
          @RequestPart(value = RequestParams.ARTIFACT_ACTIONS, required = false) Set<String> artifactActions,
          @ApiParam(value = "Artifact file", required = true)
          @RequestPart(RequestParams.ARTIFACT_FILE) MultipartFile artifactFile) {
    ArtifactDTO addedArtifact = service.addArtifact(modelId, artifactActions, artifactFile);
    HttpHeaders httpHeaders = new HttpHeaders();
    addArtifactLocation(modelId, addedArtifact, httpHeaders);
    return new ResponseEntity<>(addedArtifact, httpHeaders, HttpStatus.CREATED);
  }

  @ApiOperation(
          value = "Returns artifact metadata",
          notes = "Privilege level: Consumer of this endpoint must have a valid access token"
  )
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "SUCCESS"),
          @ApiResponse(code = 404, message = "Model or artifact not Found"),
          @ApiResponse(code = 500, message =
                  "Internal server error, e.g. error getting artifact metadata"),
  })
  @RequestMapping(
          value = ModelCatalogPaths.ARTIFACT,
          method = RequestMethod.GET,
          produces = RequestParams.CONTENT_TYPE_APP_JSON_UTF
  )
  public ArtifactDTO retrieveArtifact(
          @ApiParam(value = "Model id", required = true) @PathVariable UUID modelId,
          @ApiParam(value = "Artifact id", required = true) @PathVariable UUID artifactId) {
    return service.retrieveArtifact(modelId, artifactId);
  }

  @ApiOperation(
          value = "Downloads artifact file",
          notes = "Privilege level: Consumer of this endpoint must have a valid access token"
  )
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "SUCCESS"),
          @ApiResponse(code = 404, message = "Model or artifact not Found"),
          @ApiResponse(code = 500, message =
                  "Internal server error, e.g. error getting artifact file"),
  })
  @RequestMapping(
          value = ModelCatalogPaths.ARTIFACT_FILE,
          method = RequestMethod.GET,
          produces = {
              RequestParams.CONTENT_TYPE_APP_JSON_UTF,
              RequestParams.CONTENT_TYPE_APP_OCTET_STREAM,
          }
  )
  public Resource retrieveArtifactFile(
          @ApiParam(value = "Model id", required = true) @PathVariable UUID modelId,
          @ApiParam(value = "Artifact id", required = true) @PathVariable UUID artifactId,
          HttpServletResponse response) throws IOException {
    // Get file's input stream
    InputStream istream = service.retrieveArtifactFile(modelId, artifactId);

    // Set response headers
    ArtifactDTO artifact = service.retrieveArtifact(modelId, artifactId);
    response.addHeader("Content-disposition", "attachment; filename=" + artifact.getFilename());

    return new InputStreamResource(istream);
  }

  @ApiOperation(
          value = "Deletes artifact",
          notes = "Privilege level: Consumer of this endpoint must have a valid access token"
  )
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Deleted"),
          @ApiResponse(code = 404, message = "Model or artifact not Found"),
          @ApiResponse(code = 500, message =
                  "Internal server error, e.g. error saving model metadata"),
  })
  @RequestMapping(
          value = ModelCatalogPaths.ARTIFACT,
          method = RequestMethod.DELETE,
          produces = RequestParams.CONTENT_TYPE_APP_JSON_UTF)
  public ArtifactDTO deleteArtifact(
          @ApiParam(value = "Model id", required = true) @PathVariable UUID modelId,
          @ApiParam(value = "Artifact id", required = true) @PathVariable UUID artifactId) {
    return service.deleteArtifact(modelId, artifactId);
  }

  private void addArtifactLocation(UUID modelId, ArtifactDTO artifact, HttpHeaders httpHeaders) {
    String locationString = ModelCatalogPaths.pathToModelArtifact(modelId, artifact.getId());
    URI location = URI.create(locationString);
    httpHeaders.setLocation(location);
  }

}
