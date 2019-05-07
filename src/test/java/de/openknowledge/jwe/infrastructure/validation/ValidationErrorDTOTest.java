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
package de.openknowledge.jwe.infrastructure.validation;

import de.openknowledge.jwe.infrastructure.domain.entity.AbstractEntity;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/*
 * Test class for the DTO {@link ValidationErrorDTO}.
 */
public class ValidationErrorDTOTest {

  @Before
  public void setUp() {
    Locale.setDefault(Locale.ENGLISH);
  }

  @Test
  public void instantiationShouldFailForMissingConstraintViolation() {
    assertThatNullPointerException()
        .isThrownBy(() -> new ValidationErrorDTO(null))
        .withMessage("constraintViolation must not be null")
        .withNoCause();
  }

  @Test
  public void instantiationShouldSucceedWithPayload() {
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    Validator validator = validatorFactory.getValidator();

    TestEntityA entity = new TestEntityA();

    Set<ConstraintViolation<TestEntityA>> constraintViolations = validator.validate(entity);
    assertThat(constraintViolations).hasSize(1);

    ValidationErrorDTO validationError = new ValidationErrorDTO(constraintViolations.iterator().next());
    Assertions.assertThat(validationError.getCode()).isEqualTo("VALUE_IS_NULL");
    Assertions.assertThat(validationError.getMessage()).isEqualTo("value may not be null");
  }

  @Test
  public void instantiationShouldSucceedWithoutPayload() {
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    Validator validator = validatorFactory.getValidator();

    TestEntityB entity = new TestEntityB();

    Set<ConstraintViolation<TestEntityB>> constraintViolations = validator.validate(entity);
    assertThat(constraintViolations).hasSize(1);

    ValidationErrorDTO validationError = new ValidationErrorDTO(constraintViolations.iterator().next());
    Assertions.assertThat(validationError.getCode()).isEqualTo("UNKNOWN");
    Assertions.assertThat(validationError.getMessage()).isEqualTo("value may not be null");
  }

  private static class TestEntityA extends AbstractEntity<Long> {

    private Long id;

    @NotNull(payload = TestValueIsNull.class)
    private String value;

    @Override
    public Long getId() {
      return id;
    }

    public String getValue() {
      return value;
    }
  }

  private static class TestEntityB extends AbstractEntity<Long> {

    private Long id;

    @NotNull
    private String value;

    @Override
    public Long getId() {
      return id;
    }

    public String getValue() {
      return value;
    }
  }

}
