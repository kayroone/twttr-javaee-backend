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
package de.openknowledge.jwe.infrastructure.domain.value;

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import de.openknowledge.jwe.AbstractArchUnitTest;
import org.junit.Test;

/**
 * ArchUnit test for ValueObject classes
 */
public class ValueObjectArchUnitTest extends AbstractArchUnitTest {

  @Test
  public void classesShouldBeAssignableToValueObject() {
    ArchRuleDefinition.classes()
        .that()
        .haveSimpleNameEndingWith("DTO")
        .should()
        .beAssignableTo(AbstractValueObject.class)
        .orShould()
        .beAssignableTo(AbstractSimpleValueObject.class)
        .check(classes);
  }

  @Test
  public void classesShouldResideInDomainLayer() {
    ArchRuleDefinition.classes()
        .that()
        .areAssignableTo(AbstractValueObject.class)
        .or()
        .areAssignableTo(AbstractSimpleValueObject.class)
        .should()
        .resideInAnyPackage("..application..", "..domain..", "..infrastructure.validation..")
        .check(classes);
  }
}
