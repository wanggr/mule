/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.config.internal.dsl.model;

import static java.lang.Thread.currentThread;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import org.mule.tck.junit4.AbstractMuleTestCase;

public class ClassLoaderResourceProviderTestCase extends AbstractMuleTestCase {

  private ClassLoaderResourceProvider resourceProvider;

  @Before
  public void setUp() {
    resourceProvider = new ClassLoaderResourceProvider(currentThread().getContextClassLoader());
  }

  @Test
  public void sameJarResourceGetsLoaded() {
    verifyResourceGetsLoadedSuccessfully(Paths.get("META-INF", "mule-core.xsd").toString());
  }

  @Test
  public void differentJarResourceGetsLoaded() {
    verifyResourceGetsLoadedSuccessfully(Paths.get("javax", "inject", "Inject.class").toString());
  }

  @Test
  public void absolutePathResourceGetsLoaded() {
    File file = FileUtils.toFile(getClass().getClassLoader().getResource(Paths.get("META-INF", "mule-core.xsd").toString()));
    verifyResourceGetsLoadedSuccessfully(file.getAbsolutePath());
  }

  private void verifyResourceGetsLoadedSuccessfully(String resource) {
    try (InputStream content = resourceProvider.getResourceAsStream(resource)) {
      assertThat(content, notNullValue());
    } catch (IOException e) {
      fail();
    }
  }

}
