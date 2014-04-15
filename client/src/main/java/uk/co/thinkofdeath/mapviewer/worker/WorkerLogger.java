/*
 * Copyright 2014 Matthew Collins
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

package uk.co.thinkofdeath.mapviewer.worker;

import uk.co.thinkofdeath.mapviewer.shared.logging.Logger;
import uk.co.thinkofdeath.mapviewer.shared.logging.LoggerFactory;

public class WorkerLogger implements LoggerFactory {

    // No logging for workers
    private final Logger logger = new Logger() {
        @Override
        public void debug(Object o) {

        }

        @Override
        public void info(Object o) {

        }

        @Override
        public void warn(Object o) {

        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public Logger getLogger(String name) {
        return logger;
    }
}
