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
package org.trustedanalytics.modelcatalog.rest.client.http;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.core.io.Resource;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public final class HttpFileResource implements Resource, Closeable {

  private final HttpRequestBase request;
  private final CloseableHttpResponse response;
  private final String defaultFilename;
  private boolean isOpen = true;

  public HttpFileResource(
      HttpRequestBase request, CloseableHttpResponse response, String defaultFilename) {
    this.request = request;
    this.response = response;
    this.defaultFilename = defaultFilename;
  }

  @Override
  public boolean exists() {
    return true;
  }

  @Override
  public boolean isReadable() {
    return response.getEntity().isStreaming();
  }

  @Override
  public boolean isOpen() {
    return isOpen;
  }

  @Override
  public URL getURL() throws IOException {
    return getURI().toURL();
  }

  @Override
  public URI getURI() throws IOException {
    return request.getURI();
  }

  @Override
  public File getFile() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public long contentLength() throws IOException {
    return response.getEntity().getContentLength();
  }

  @Override
  public long lastModified() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Resource createRelative(String s) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getFilename() {
    // Try to obtain the filename from HTTP headers
    Header[] headers = response.getHeaders("Content-disposition");
    for (Header header : headers) {
      String[] splitHeaderValue = header.getValue().split("filename\\s*=");
      if (splitHeaderValue.length == 2) {
        return splitHeaderValue[1].trim();
      }
    }

    // If header not found, return the default filename
    return defaultFilename;
  }

  @Override
  public String getDescription() {
    throw new UnsupportedOperationException();
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return response.getEntity().getContent();
  }

  @Override
  public void close() throws IOException {
    HttpClientWrapper.releaseResources(request, response);
    isOpen = false;
  }
}
