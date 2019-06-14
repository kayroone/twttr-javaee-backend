/*
 * Copyright (C) open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package de.openknowledge.jwe.application;

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import de.openknowledge.jwe.AbstractArchUnitTest;
import org.junit.Test;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * ArchUnit test for JAX-RS activator classes.
 */
public class JaxrsActivatorArchUnitTest extends AbstractArchUnitTest {

  @Test
  public void classesShouldBeAnnotatedWithApplicationPath() {
    ArchRuleDefinition.classes()
        .that()
        .areAssignableFrom(Application.class)
        .should()
        .beAnnotatedWith(ApplicationPath.class)
        .check(classes);
  }

  @Test
  public void classesShouldBeAssignableToApplication() {
    ArchRuleDefinition.classes()
        .that()
        .areAnnotatedWith(ApplicationPath.class)
        .should()
        .beAssignableTo(Application.class)
        .check(classes);
  }

  @Test
  public void classesShouldHaveNameJaxRsActivator() {
    ArchRuleDefinition.classes()
        .that()
        .areAnnotatedWith(ApplicationPath.class)
        .should()
        .haveSimpleName("JaxRsActivator")
        .check(classes);
  }

  @Test
  public void classesShouldResideInApplicationLayer() {
    ArchRuleDefinition.classes()
        .that()
        .areAnnotatedWith(ApplicationPath.class)
        .should()
        .resideInAPackage("..application..")
        .check(classes);
  }
}
