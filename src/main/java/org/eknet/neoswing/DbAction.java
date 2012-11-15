/*
 * Copyright 2012 Eike Kettner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eknet.neoswing;

import org.eknet.neoswing.utils.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 15.11.12 20:35
 */
public abstract class DbAction<A, B> extends SwingWorker<A, B> {

  private static final Logger log = LoggerFactory.getLogger(DbAction.class);

  private GraphModel model;

  public GraphModel getModel() {
    return model;
  }

  public void setModel(GraphModel model) {
    this.model = model;
  }

  @Override
  protected final A doInBackground() throws Exception {
    GraphDb.Tx tx = getModel().getDatabase().beginTx();
    try {
      A a = doInTx(getModel());
      tx.success();
      return a;
    } catch (Exception e) {
      log.error("Error in db action", e);
      throw e;
    } finally {
      tx.finish();
    }
  }

  protected abstract A doInTx(GraphModel model);

  protected A safeGet() {
    try {
      return get(30, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("Interrupted while waiting for result", e);
      Dialogs.error(null, e.getMessage());
    } catch (ExecutionException e) {
      log.error("Error while executing worker!", e.getCause());
      Dialogs.error(null, e.getCause().getMessage());
    } catch (TimeoutException e) {
      log.error("Timeout while waiting for result!", e);
      Dialogs.error(null, e.getMessage());
    }
    return null;
  }

}
