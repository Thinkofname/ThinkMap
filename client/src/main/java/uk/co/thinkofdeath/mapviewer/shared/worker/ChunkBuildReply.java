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
import com.google.gwt.core.client.JsArray;
import uk.co.thinkofdeath.mapviewer.shared.model.SendableModel;
import uk.co.thinkofdeath.mapviewer.shared.support.TUint8Array;

import java.util.List;

public class ChunkBuildReply extends JavaScriptObject {
    protected ChunkBuildReply() {
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
     * @param models
     *         The transparent models for this section
     * @return The message
     */
    public static native ChunkBuildReply create(int x, int z, int i, int buildNumber,
                                                TUint8Array data, List<SendableModel> models)/*-{
        var jms = null;
        if (models != null) {
            jms = [];
            var size = models.@java.util.List::size()();
            for (var j = 0; j < size; j++) {
                var m = models.@java.util.List::get(I)(j);
                if (m != null) {
                    jms.push(m);
                }
            }
        }
        return {x: x, z: z, i: i, buildNumber: buildNumber, data: data, trans: jms};
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

    /**
     * Returns the data (built model)
     *
     * @return The data
     */
    public final native TUint8Array getData()/*-{
        return this.data;
    }-*/;

    /**
     * Returns the internal data structure of a bsp tree containing the transparent blocks
     *
     * @return The BSPData for transparent blocks
     */
    public final native JsArray<SendableModel> getTransparentData()/*-{
        return this.trans;
    }-*/;
}
