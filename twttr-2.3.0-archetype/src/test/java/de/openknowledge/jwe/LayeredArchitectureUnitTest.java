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
package de.openknowledge.jwe;

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.Architectures;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

import org.junit.Test;

/**
 * ArchUnit test for the layered architecture.
 */
public class LayeredArchitectureUnitTest extends AbstractArchUnitTest {

  @Test
  public void classesShouldFollowTheApplicationLayerAccessRule() {
    ArchRuleDefinition.classes()
        .that()
        .resideInAPackage(PACKAGE_ROOT + ".application..")
        .should()
        .onlyBeAccessed()
        .byAnyPackage(PACKAGE_ROOT + ".application..")
        .check(classes);
  }

  @Test
  public void classesShouldFollowTheDomainLayerAccessRule() {
    ArchRuleDefinition.classes()
        .that()
        .resideInAPackage(PACKAGE_ROOT + ".domain..")
        .should()
        .onlyBeAccessed()
        .byAnyPackage(PACKAGE_ROOT + ".application..", PACKAGE_ROOT + ".domain..")
        .check(classes);
  }

  @Test
  public void classesShouldFollowTheInfrastructureLayerAccessRule() {
    ArchRuleDefinition.classes()
        .that()
        .resideInAPackage(PACKAGE_ROOT + ".infrastructure..")
        .should()
        .onlyBeAccessed()
        .byAnyPackage(PACKAGE_ROOT + ".application..", PACKAGE_ROOT + ".domain..", PACKAGE_ROOT + ".infrastructure..")
        .check(classes);
  }

  @Test
  public void layeredArchitecture() {
    Architectures.layeredArchitecture()
        .layer("Application").definedBy(PACKAGE_ROOT + ".application..")
        .layer("Domain").definedBy(PACKAGE_ROOT + ".domain..")
        .layer("Infrastructure").definedBy(PACKAGE_ROOT + ".infrastructure..")
        .layer("Base").definedBy(PACKAGE_ROOT + ".infrastructure.domain..")
        .whereLayer("Application").mayNotBeAccessedByAnyLayer()
        .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application")
        .whereLayer("Infrastructure").mayOnlyBeAccessedByLayers("Application", "Domain")
        .whereLayer("Base").mayOnlyBeAccessedByLayers("Application", "Domain", "Infrastructure")
        .check(classes);
  }

  @Test
  public void layersShouldBeFreeOfCycles() {
    SlicesRuleDefinition.slices()
        .matching(PACKAGE_ROOT + ".(*)..")
        .should()
        .beFreeOfCycles()
        .check(classes);
  }
}
