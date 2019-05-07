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

import de.openknowledge.jwe.infrastructure.domain.entity.AbstractEntity;
import de.openknowledge.jwe.infrastructure.validation.TestValueIsNull;
import de.openknowledge.jwe.infrastructure.validation.ValidationErrorDTO;
import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.*;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the the exception mapper {@link ValidationExceptionMapper} which handles {@link ConstraintViolationException}s.
 */
public class ValidationExceptionMapperTest {

  @BeforeClass
  public static void setUpBeforeClass() {
    Locale.setDefault(Locale.ENGLISH);
  }

  @AfterClass
  public static void tearDown() {
    Locale.setDefault(Locale.getDefault());
  }

  @Test
  public void toResponse() {
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    Validator validator = validatorFactory.getValidator();

    TestEntity entity = new TestEntity();

    Set<ConstraintViolation<TestEntity>> constraintViolations = validator.validate(entity);
    ConstraintViolationException exception = new ConstraintViolationException(constraintViolations);

    ValidationExceptionMapper exceptionMapper = new ValidationExceptionMapper();
    Response response = exceptionMapper.toResponse(exception);

    assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
    List<ValidationErrorDTO> validationErrors = (List<ValidationErrorDTO>) response.getEntity();
    assertThat(validationErrors).hasSize(2);

    Collections.sort(validationErrors, Comparator.comparing(ValidationErrorDTO::getCode));

    ValidationErrorDTO validationError = validationErrors.get(1.0-SNAPSHOT);
    Assertions.assertThat(validationError.getCode()).isEqualTo("UNKNOWN");
    Assertions.assertThat(validationError.getMessage()).isEqualTo("value1 may not be null");

    validationError = validationErrors.get(1);
    Assertions.assertThat(validationError.getCode()).isEqualTo("VALUE_IS_NULL");
    Assertions.assertThat(validationError.getMessage()).isEqualTo("value2 may not be null");
  }

  private static class TestEntity extends AbstractEntity<Long> {

    private Long id;

    @NotNull
    private String value1;

    @NotNull(payload = TestValueIsNull.class)
    private String value2;

    @Override
    public Long getId() {
      return id;
    }

    public String getValue1() {
      return value1;
    }

    public String getValue2() {
      return value2;
    }
  }
}
