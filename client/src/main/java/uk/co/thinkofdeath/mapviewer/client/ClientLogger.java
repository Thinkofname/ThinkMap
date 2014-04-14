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

package uk.co.thinkofdeath.mapviewer.client;

import elemental.client.Browser;
import uk.co.thinkofdeath.mapviewer.shared.logging.Logger;
import uk.co.thinkofdeath.mapviewer.shared.logging.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ClientLogger implements LoggerFactory {

    public static final int DEBUG = 0;
    public static final int INFO = 1;
    public static final int WARN = 2;

    private final int logLevel;
    private final Map<String, Logger> loggers = new HashMap<>();

    /**
     * Creates a logger which logs to the javascript console
     *
     * @param logLevel The smallest log level to log
     *                 /
     */
    public ClientLogger(int logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Logger getLogger(String name) {
        if (loggers.containsKey(name)) {
            return loggers.get(name);
        }
        Logger logger = new LoggerImpl();
        loggers.put(name, logger);
        return logger;
    }

    private class LoggerImpl implements Logger {

        /**
         * {@inheritDoc}
         */
        @Override
        public void debug(Object o) {
            if (logLevel <= DEBUG) {
                Browser.getWindow().getConsole().debug(o);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void info(Object o) {
            if (logLevel <= INFO) {
                Browser.getWindow().getConsole().log(o);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void warn(Object o) {
            if (logLevel <= WARN) {
                Browser.getWindow().getConsole().warn(o);
            }
        }
    }
}
