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

import de.jwiegmann.twttr.infrastructure.domain.value.AbstractValueObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import static org.apache.commons.lang3.Validate.notNull;

/** A DTO that represents an application error. */
@Schema
public class ApplicationErrorDTO extends AbstractValueObject {

  @Schema(example = "UNKNOWN_ERROR")
  private String code;

  @Schema(example = "An unknown error occurred")
  private String message;

  public ApplicationErrorDTO() {
    super();
  }

  public ApplicationErrorDTO(final ApplicationError error, final String message) {
    this();
    this.code = notNull(error, "errorCode must not be null").getErrorCode();
    this.message = notNull(message, "message must not be null");
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  @Override
  protected Object[] values() {
    return new Object[] {code, message};
  }

  @Override
  public String toString() {
    return "ApplicationErrorDTO{" + "code='" + code + '\'' + ", message='" + message + '\'' + '}';
  }
}
