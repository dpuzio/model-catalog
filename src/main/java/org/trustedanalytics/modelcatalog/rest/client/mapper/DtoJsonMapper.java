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
package org.trustedanalytics.modelcatalog.rest.client.mapper;

import org.trustedanalytics.modelcatalog.rest.client.ModelCatalogClientException;
import org.trustedanalytics.modelcatalog.rest.entities.ArtifactDTO;
import org.trustedanalytics.modelcatalog.rest.entities.ModelDTO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class DtoJsonMapper {

  private final ObjectMapper objectMapper;

  public DtoJsonMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public Collection<ModelDTO> toModelDTOCollection(String json) {
    return extractDTOCollectionFromEntity(json);
  }

  public ModelDTO toModelDTO(String json) {
    return extractDTOFromEntity(json, ModelDTO.class);
  }

  public ArtifactDTO toArtifactDTO(String json) {
    return extractDTOFromEntity(json, ArtifactDTO.class);
  }

  public String toJSON(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ModelCatalogClientException("Cannot serialize object", e);
    }
  }

  private <T> T extractDTOFromEntity(String json, Class<T> entityClass) {
    T dto;
    try {
      dto = objectMapper.readValue(json, entityClass);
    } catch (IOException e) {
      throw new ModelCatalogClientException("Cannot parse response entity", e);
    }

    return dto;
  }

  private <T> Collection<T> extractDTOCollectionFromEntity(String json) {
    Collection<T> dto;
    try {
      dto = objectMapper.readValue(json, new TypeReference<List<T>>(){});
    } catch (IOException e) {
      throw new ModelCatalogClientException("Cannot parse response entity", e);
    }

    return dto;
  }
}
