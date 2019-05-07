/*
 * Copyright (C) open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.1.0-SNAPSHOT (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.1.0-SNAPSHOT
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package de.openknowledge.jwe.infrastructure.rest.exception;

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

import de.openknowledge.jwe.AbstractArchUnitTest;

import org.junit.Test;

import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * ArchUnit test for exception mapper classes.
 */
public class ExceptionMapperArchUnitTest extends AbstractArchUnitTest {

  @Test
  public void classesShouldBeAnnotatedWithProvider() {
    ArchRuleDefinition.classes()
        .that()
        .implement(ExceptionMapper.class)
        .should()
        .beAnnotatedWith(Provider.class)
        .check(classes);
  }

  @Test
  public void classesShouldHaveSuffixExceptionMapper() {
    ArchRuleDefinition.classes()
        .that()
        .implement(ExceptionMapper.class)
        .should()
        .haveSimpleNameEndingWith("ExceptionMapper")
        .check(classes);
  }

  @Test
  public void classesShouldImplementExceptionMapper() {
    ArchRuleDefinition.classes()
        .that()
        .haveSimpleNameEndingWith("ExceptionMapper")
        .should()
        .implement(ExceptionMapper.class)
        .check(classes);
  }

  @Test
  public void classesShouldResideInInfrastructureLayer() {
    ArchRuleDefinition.classes()
        .that()
        .implement(ExceptionMapper.class)
        .should()
        .resideInAPackage(PACKAGE_ROOT + ".infrastructure.rest.exception..")
        .check(classes);
  }
}
