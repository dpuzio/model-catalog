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
package org.trustedanalytics.modelcatalog.rest.api;

import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collection;

public interface ModelProviderRestApi {

    String GET_ALL_MODELS_URL = "/rest/v0/models";
    String GET_MODEL_METADATA_URL = "/rest/v0/models/{modelId}/metadata";
    String GET_MODEL_URL = "/rest/v0/models/{modelId}";

    @RequestMapping(value = GET_ALL_MODELS_URL, method = RequestMethod.GET)
    Collection<ModelMetadata> listModels();

    @RequestMapping(value = GET_MODEL_METADATA_URL, method = RequestMethod.GET)
    ModelMetadata getModelMetadata(@PathVariable String modelId);

    @RequestMapping(value = GET_MODEL_URL, method = RequestMethod.GET)
    FileSystemResource downloadModel(@PathVariable String modelId);

}
