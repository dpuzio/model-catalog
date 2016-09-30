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
package org.trustedanalytics.modelcatalog.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.trustedanalytics.modelcatalog.TestModelsBuilder;
import org.trustedanalytics.modelcatalog.domain.Model;

import com.mongodb.WriteConcernException;
import com.mongodb.WriteResult;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class MongoModelStoreTest {

  private static final String ID = "_id";
  private static final String EXEMPLARY_PROPERTY_NAME = "exemplary property name";
  private static final String EXEMPLARY_PROPERTY_VALUE = "exemplary property value";

  @Mock
  private MongoOperations mongoOperations;

  @InjectMocks
  private MongoModelStore mongoModelStore;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Captor
  private ArgumentCaptor<Query> queryCaptor;
  @Captor
  private ArgumentCaptor<Update> updateStmtCaptor;

  @Test
  public void shouldListModels() throws ModelStoreException {
    // when
    mongoModelStore.listModels(UUID.randomUUID());
    // then
    verify(mongoOperations).findAll(Model.class);
  }

  @Test
  public void shouldRetrieveModelWithGivenId() throws ModelStoreException {
    // given
    final UUID modelId = UUID.randomUUID();
    // when
    mongoModelStore.retrieveModel(modelId);
    // then
    verify(mongoOperations).findOne(queryCaptor.capture(), modelClassMatcher());
    assertThatQueryContainsId(modelId);
  }

  @Test
  public void shouldAddModel_withNoExceptions() throws ModelStoreException {
    mongoModelStore.addModel(TestModelsBuilder.emptyModel(), UUID.randomUUID());
  }

  @Test
  public void addModel_shouldForwardExceptions() {
    // given
    doThrow(mock(WriteConcernException.class)).when(mongoOperations).insert(any(Model.class));
    // when, then
    assertThatExceptionOfType(ModelStoreException.class)
            .isThrownBy(() -> mongoModelStore.addModel(
                    TestModelsBuilder.emptyModel(), UUID.randomUUID()));
  }

  @Test
  public void shouldUpdateModel() throws ModelStoreException {
    // given
    UUID modelId = UUID.randomUUID();
    Map<String, Object> propertiesToUpdate = preparePropertiesToUpdateMap();
    WriteResult writeResult = mock(WriteResult.class);
    when(writeResult.getN()).thenReturn(1);
    when(mongoOperations.updateFirst(any(), any(), modelClassMatcher()))
            .thenReturn(writeResult);
    // when
    mongoModelStore.updateModel(modelId, propertiesToUpdate);
    // then
    verify(mongoOperations).updateFirst(
            queryCaptor.capture(), updateStmtCaptor.capture(), modelClassMatcher());
    assertThatQueryContainsId(modelId);
    assertThatUpdateStmtContainsPassedProperty();
  }

  @Test
  public void updateModel_shouldReturnSuccess_whenAtLeastOneDocumentUpdated() {
    // given
    WriteResult writeResultMock = mock(WriteResult.class);
    when(mongoOperations.updateFirst(any(), any(), modelClassMatcher())).thenReturn
            (writeResultMock);
    when(writeResultMock.getN()).thenReturn(5);
    // when/then
    try {
      mongoModelStore.updateModel(UUID.randomUUID(), preparePropertiesToUpdateMap());
    } catch (ModelStoreException e) {
      Assertions.fail("Unexpected exception", e);
    }
  }

  @Test(expected = ModelStoreException.class)
  public void updateModel_shouldReturnFailure_whenNoDocumentsUpdated() throws ModelStoreException {
    // given
    WriteResult writeResultMock = mock(WriteResult.class);
    when(mongoOperations.updateFirst(any(), any(), modelClassMatcher())).thenReturn
            (writeResultMock);
    when(writeResultMock.getN()).thenReturn(0);
    // when
    mongoModelStore.updateModel(UUID.randomUUID(), preparePropertiesToUpdateMap());
    Assertions.fail("Expected exception");
  }

  @Test
  public void shouldDeleteModel() throws ModelStoreException {
    // given
    UUID modelId = UUID.randomUUID();
    WriteResult writeResult = mock(WriteResult.class);
    when(writeResult.getN()).thenReturn(1);
    when(mongoOperations.remove(any(), modelClassMatcher()))
            .thenReturn(writeResult);
    // when
    mongoModelStore.deleteModel(modelId);
    // then
    verify(mongoOperations).remove(queryCaptor.capture(), modelClassMatcher());
    assertThatQueryContainsId(modelId);
  }

  @Test
  public void deleteModel_shouldReturnSuccess_whenAtLeastOneDocumentDeleted()
          throws ModelStoreException {
    // given
    WriteResult writeResultMock = mock(WriteResult.class);
    when(mongoOperations.remove(any(), modelClassMatcher())).thenReturn(writeResultMock);
    when(writeResultMock.getN()).thenReturn(5);
    // when / then
    mongoModelStore.deleteModel(UUID.randomUUID());
  }

  @Test(expected = ModelStoreException.class)
  public void deleteModel_shouldReturnFailure_whenNoDocumentsDeleted() throws ModelStoreException {
    // given
    WriteResult writeResultMock = mock(WriteResult.class);
    when(mongoOperations.remove(any(), modelClassMatcher())).thenReturn(writeResultMock);
    when(writeResultMock.getN()).thenReturn(0);
    // when / then
    mongoModelStore.deleteModel(UUID.randomUUID());
  }

  private Class<Model> modelClassMatcher() {
    return Matchers.<Class<Model>>any();
  }

  private void assertThatQueryContainsId(UUID modelId) {
    Query capturedQuery = queryCaptor.getValue();
    assertThat(capturedQuery).isEqualToComparingFieldByFieldRecursively(
            new Query(where(ID).is(modelId)));
  }

  private Map<String, Object> preparePropertiesToUpdateMap() {
    HashMap<String, Object> properties = new HashMap<>();
    properties.put(EXEMPLARY_PROPERTY_NAME, EXEMPLARY_PROPERTY_VALUE);
    return properties;
  }

  private void assertThatUpdateStmtContainsPassedProperty() {
    Update captureUpdateStmt = updateStmtCaptor.getValue();
    assertThat(captureUpdateStmt).isEqualToComparingFieldByFieldRecursively(
            new Update().set(EXEMPLARY_PROPERTY_NAME, EXEMPLARY_PROPERTY_VALUE)
    );
  }

}