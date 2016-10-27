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

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, FongoConfig.class})
@WebAppConfiguration
@IntegrationTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles({"integration-test", "in-memory"})
public class SwaggerIT {

  @Value("localhost:${local.server.port}")
  private String swaggerHost;

  @Value("http://localhost:${local.server.port}/v2/api-docs")
  private String swaggerUrl;

  @Test
  public void swagger_shouldGenerateJson() throws IOException {
    // Create HTTP client to read Swagger JSON from application
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(swaggerUrl);
    CloseableHttpResponse response = httpclient.execute(httpGet);

    try {
      // Verify that there were no problem with getting JSON
      StatusLine statusLine = response.getStatusLine();
      Assert.assertEquals(HttpStatus.SC_OK, statusLine.getStatusCode());

      // Read JSON, replace the host property to remove port number
      // (it is different every time the test is executed)
      HttpEntity entity = response.getEntity();
      String swaggerJson = EntityUtils.toString(entity).replace(swaggerHost, "localhost");
      ObjectMapper mapper = new ObjectMapper();
      JsonNode node = mapper.readTree(swaggerJson);
      Assert.assertEquals("localhost", node.get("host").getTextValue());
      Assert.assertEquals("2.0", node.get("swagger").getTextValue());

      // Write JSON (pretty print) to the file in the root project directory
      String currentDir = System.getProperty("user.dir");
      ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
      Files.write(
          Paths.get(currentDir).resolve("swagger.json"),
          writer.withDefaultPrettyPrinter().writeValueAsString(node).getBytes(),
          StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    } finally {
      response.close();
    }
  }
}
