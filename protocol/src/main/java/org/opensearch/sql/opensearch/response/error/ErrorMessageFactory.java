/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.sql.opensearch.response.error;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorMessageFactory {
  /**
   * Create error message based on the exception type.
   *
   * @param e exception to create error message
   * @param status exception status code
   * @return error message
   */
  public static ErrorMessage createErrorMessage(Throwable e, int status) {
    return new ErrorMessage(e, status);
  }
}
