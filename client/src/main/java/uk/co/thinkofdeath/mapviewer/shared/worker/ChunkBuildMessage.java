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

package uk.co.thinkofdeath.mapviewer.shared.worker;

import com.google.gwt.core.client.JavaScriptObject;

public class ChunkBuildMessage extends JavaScriptObject {
    protected ChunkBuildMessage() {
    }

    /**
     * Creates a new chunk unload message
     *
     * @param x
     *         The x position of the chunk
     * @param z
     *         The z position of the chunk
     * @param i
     *         The section number
     * @param buildNumber
     *         The build number
     * @return The message
     */
    public static native ChunkBuildMessage create(int x, int z, int i, int buildNumber)/*-{
        return {x: x, z: z, i: i, buildNumber: buildNumber};
    }-*/;

    /**
     * Returns the x position of the chunk
     *
     * @return The x position
     */
    public final native int getX()/*-{
        return this.x;
    }-*/;

    /**
     * Returns the z position of the chunk
     *
     * @return The z position
     */
    public final native int getZ()/*-{
        return this.z;
    }-*/;

    /**
     * Returns the section number of the chunk
     *
     * @return The section number
     */
    public final native int getSectionNumber()/*-{
        return this.i;
    }-*/;

    /**
     * Returns this build's build number
     *
     * @return The build number
     */
    public final native int getBuildNumber()/*-{
        return this.buildNumber;
    }-*/;
}
