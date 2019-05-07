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
package de.openknowledge.jwe.infrastructure.domain.entity;

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

import de.openknowledge.jwe.AbstractArchUnitTest;

import org.junit.Test;

import javax.persistence.Entity;

/**
 * ArchUnit test for entity classes.
 */
public class EntityArchUnitTest extends AbstractArchUnitTest {

  @Test
  public void classesShouldBeAnnotatedWithEntity() {
    ArchRuleDefinition.classes()
        .that()
        .areAssignableTo(AbstractEntity.class)
        .and()
        .haveSimpleNameNotStartingWith("Abstract")
        .and()
        .haveSimpleNameNotContaining("Test")
        .should()
        .beAnnotatedWith(Entity.class)
        .check(classes);
  }

  @Test
  public void classesShouldBeAssignableToAbstractEntity() {
    ArchRuleDefinition.classes()
        .that()
        .areAnnotatedWith(Entity.class)
        .should()
        .beAssignableTo(AbstractEntity.class)
        .check(classes);
  }

  @Test
  public void classesShouldResideInDomainLayer() {
    ArchRuleDefinition.classes()
        .that()
        .areAnnotatedWith(Entity.class)
        .should()
        .resideInAPackage("..domain..")
        .check(classes);
  }
}
