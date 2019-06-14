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
package de.openknowledge.jwe.infrastructure.domain.builder;

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import de.openknowledge.jwe.AbstractArchUnitTest;
import org.junit.Test;

/**
 * ArchUnit test for builder classes.
 */
public class BuilderArchUnitTest extends AbstractArchUnitTest {

  @Test
  public void classesShouldBeAssignableToBuilder() {
    ArchRuleDefinition.classes()
        .that()
        .haveSimpleNameEndingWith("Builder")
        .and()
        .areNotInterfaces()
        .should()
        .implement(Builder.class)
        .orShould()
        .beAssignableTo(AbstractBuilder.class)
        .orShould()
        .beAssignableTo(DefaultBuilder.class)
        .orShould()
        .beAssignableTo(ObjectBuilder.class)
        .check(classes);
  }

  @Test
  public void classesShouldHaveSuffixBuilder() {
    ArchRuleDefinition.classes()
        .that()
        .implement(Builder.class)
        .or()
        .areAssignableTo(AbstractBuilder.class)
        .or()
        .areAssignableTo(DefaultBuilder.class)
        .or()
        .areAssignableTo(ObjectBuilder.class)
        .should()
        .haveSimpleNameEndingWith("Builder")
        .check(classes);
  }

  @Test
  public void classesShouldResideInDomainLayer() {
    ArchRuleDefinition.classes()
        .that()
        .implement(Builder.class)
        .or()
        .areAssignableTo(AbstractBuilder.class)
        .or()
        .areAssignableTo(DefaultBuilder.class)
        .or()
        .areAssignableTo(ObjectBuilder.class)
        .should()
        .resideInAPackage("..domain..")
        .check(classes);
  }
}
