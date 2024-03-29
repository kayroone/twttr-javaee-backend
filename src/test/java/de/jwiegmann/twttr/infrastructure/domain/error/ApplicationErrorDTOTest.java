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
package de.jwiegmann.twttr.infrastructure.domain.error;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/** Test class for the DTO {@link ApplicationErrorDTO}. */
public class ApplicationErrorDTOTest {

  @Test
  public void instantiationShouldFailForMissingCode() {
    assertThatNullPointerException()
        .isThrownBy(() -> new ApplicationErrorDTO(null, "An unknown error occurred"))
        .withMessage("errorCode must not be null")
        .withNoCause();
  }

  @Test
  public void instantiationShouldFailForMissingMessage() {
    assertThatNullPointerException()
        .isThrownBy(() -> new ApplicationErrorDTO(() -> "UNKNOWN", null))
        .withMessage("message must not be null")
        .withNoCause();
  }

  @Test
  public void instantiationShouldSucceed() {
    ApplicationErrorDTO applicationError =
        new ApplicationErrorDTO(() -> "UNKNOWN", "An unknown error occurred");
    assertThat(applicationError.getCode()).isEqualTo("UNKNOWN");
    assertThat(applicationError.getMessage()).isEqualTo("An unknown error occurred");
  }
}
