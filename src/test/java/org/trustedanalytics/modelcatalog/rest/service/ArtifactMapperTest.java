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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.trustedanalytics.modelcatalog.service.ModelServiceException;


public class ArtifactMapperTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  @Test
  public void shouldReturnEmptySet_whenArtifactsActionsAreNull() {
    assertThat(ArtifactMapper.toArtifactActionSet(null)).isEqualTo(Collections.EMPTY_SET);
  }

  @Test
  public void shouldThrowAnException_whenInvalidArtifactActionString() throws Exception {
    // given
    Set<String> artifactActions = new HashSet<>(Arrays.asList("some-action"));

    // when
    // then
    thrown.expect(ModelServiceException.class);
    ArtifactMapper.toArtifactActionSet(artifactActions);
  }
}
