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
package de.openknowledge.jwe.infrastructure.microprofiles.health;

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import de.openknowledge.jwe.AbstractArchUnitTest;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.junit.Test;

/**
 * ArchUnit test for HealthCheck classes
 */
public class HealthCheckArchUnitTest extends AbstractArchUnitTest {

  @Test
  public void classesShouldBeAnnotatedWithHealth() {
    ArchRuleDefinition.classes()
        .that()
        .areAssignableFrom(HealthCheck.class)
        .should()
        .beAnnotatedWith(Health.class)
        .check(classes);
  }

  @Test
  public void classesShouldBeAssignableToHealthCheck() {
    ArchRuleDefinition.classes()
        .that()
        .areAnnotatedWith(Health.class)
        .should()
        .beAssignableTo(HealthCheck.class)
        .check(classes);
  }

  @Test
  public void classesShouldHaveSuffixHealthCheck() {
    ArchRuleDefinition.classes()
        .that()
        .areAssignableFrom(HealthCheck.class)
        .should()
        .haveSimpleNameEndingWith("HealthCheck")
        .check(classes);
  }
}
