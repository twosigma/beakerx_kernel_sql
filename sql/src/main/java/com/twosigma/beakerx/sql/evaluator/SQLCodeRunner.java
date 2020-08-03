/*
 *  Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.sql.evaluator;

import com.twosigma.beakerx.BeakerXClient;
import com.twosigma.beakerx.TryResult;
import com.twosigma.beakerx.jvm.object.EvaluationObject;
import com.twosigma.beakerx.sql.ReadVariableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.Callable;

import static com.twosigma.beakerx.evaluator.BaseEvaluator.INTERUPTED_MSG;

class SQLCodeRunner implements Callable<TryResult> {

  private final static Logger logger = LoggerFactory.getLogger(SQLCodeRunner.class.getName());

  private SQLEvaluator sqlEvaluator;
  private final EvaluationObject simpleEvaluationObject;
  private final BeakerXClient namespaceClient;

  SQLCodeRunner(SQLEvaluator sqlEvaluator, EvaluationObject seo, BeakerXClient namespaceClient) {
    this.sqlEvaluator = sqlEvaluator;
    this.simpleEvaluationObject = seo;
    this.namespaceClient = namespaceClient;
  }

  @Override
  public TryResult call() throws Exception {
    TryResult either;
    try {
      Object r = sqlEvaluator.executeQuery(simpleEvaluationObject.getExpression(), namespaceClient, sqlEvaluator.defaultConnectionString, sqlEvaluator.namedConnectionString);
      either = TryResult.createResult(r);
    } catch (SQLException e) {
      either = TryResult.createError(createErrorMessage(e));
    } catch (ThreadDeath e) {
      either = TryResult.createError(INTERUPTED_MSG);
    } catch (ReadVariableException e) {
      either = TryResult.createError(e.getMessage());
    } catch (Throwable e) {
      logger.error(e.getMessage());
      either = TryResult.createError(e.toString());
    }
    return sqlEvaluator.processResult(either);
  }

  private String createErrorMessage(SQLException e) {
    String err = e.toString();
    if (e.getCause() != null) {
      err = err + ", " + e.getCause().toString();
    }
    return err;
  }
}
