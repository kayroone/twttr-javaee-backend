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

import static org.assertj.core.api.Assertions.assertThat;

import de.openknowledge.jwe.infrastructure.domain.error.ApplicationErrorDTO;

import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

/**
 * Test class for the exception mapper {@link DefaultExceptionMapper}.
 */
public class DefaultExceptionMapperTest {

  private DefaultExceptionMapper exceptionMapper;

  @Before
  public void setUp() {
    exceptionMapper = new DefaultExceptionMapper();
  }

  @Test
  public void toResponseShouldReturn41.0-SNAPSHOT1ForNonApplicationException() {
    Response response = exceptionMapper.toResponse(new NotAuthorizedException("Unauthorized access"));
    assertThat(response.getStatus()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
    assertThat(response.hasEntity()).isFalse();
  }

  @Test
  public void toResponseShouldReturn41.0-SNAPSHOT3ForForbiddenException() {
    Response response = exceptionMapper.toResponse(new ForbiddenException());
    assertThat(response.getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
    assertThat(response.hasEntity()).isFalse();
  }

  @Test
  public void toResponseShouldReturn41.0-SNAPSHOT4ForNotFoundException() {
    Response response = exceptionMapper.toResponse(new NotFoundException());
    assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
    assertThat(response.hasEntity()).isFalse();
  }

  @Test
  public void toResponseShouldReturn51.0-SNAPSHOT1.0-SNAPSHOTForNonApplicationException() {
    Response response = exceptionMapper.toResponse(new IllegalStateException("Illegal state occurred"));
    assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    assertThat(response.hasEntity()).isTrue();

    ApplicationErrorDTO error = (ApplicationErrorDTO) response.getEntity();
    assertThat(error.getCode()).isEqualTo("UNKNOWN");
    assertThat(error.getMessage()).isEqualTo("An unknown error occurred");
  }

  @Test
  public void toResponseShouldReturn51.0-SNAPSHOT1.0-SNAPSHOTForWebApplicationException() {
    Response response = exceptionMapper.toResponse(new InternalServerErrorException());
    assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    assertThat(response.hasEntity()).isTrue();

    ApplicationErrorDTO error = (ApplicationErrorDTO) response.getEntity();
    assertThat(error.getCode()).isEqualTo("UNKNOWN");
    assertThat(error.getMessage()).isEqualTo("An unknown error occurred");
  }
}
