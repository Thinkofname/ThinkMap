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

package uk.co.thinkofdeath.mapviewer.shared;

import uk.co.thinkofdeath.mapviewer.shared.block.BlockRegistry;
import uk.co.thinkofdeath.mapviewer.shared.logging.LoggerFactory;

public interface IMapViewer {

    /**
     * Returns the block registry used by this map viewer
     *
     * @return The block registry
     */
    public BlockRegistry getBlockRegistry();

    /**
     * Returns the logger factory used by this map viewer
     *
     * @return The logger factory
     */
    public LoggerFactory getLoggerFactory();
}
