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

package uk.co.thinkofdeath.thinkcraft.shared.worker;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import uk.co.thinkofdeath.thinkcraft.shared.model.PositionedModel;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.ReadSerializer;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.WriteSerializer;
import uk.co.thinkofdeath.thinkcraft.shared.support.TUint8Array;

public class ChunkBuildReply extends WorkerMessage {
    private int x;
    private int z;
    private int sectionNumber;
    private int buildNumber;
    private JsArrayInteger accessData;
    private TUint8Array data;
    private TUint8Array transData;
    private JsArray<PositionedModel> modelJsArray;

    ChunkBuildReply() {
    }

    /**
     * Creates a new chunk unload message
     *
     * @param x
     *         The x position of the chunk
     * @param z
     *         The z position of the chunk
     * @param sectionNumber
     *         The section number
     * @param buildNumber
     *         The build number
     * @param accessData
     *         The section's access data
     * @param data
     *         The non-transparent block data
     * @param transData
     *         The transparent block data
     * @param modelJsArray
     *         The transparent block models
     * @return The message
     */
    public ChunkBuildReply(int x, int z, int sectionNumber, int buildNumber, JsArrayInteger accessData,
                           TUint8Array data, TUint8Array transData, JsArray<PositionedModel> modelJsArray) {
        this.x = x;
        this.z = z;
        this.sectionNumber = sectionNumber;
        this.buildNumber = buildNumber;
        this.accessData = accessData;
        this.data = data;
        this.transData = transData;
        this.modelJsArray = modelJsArray;
    }

    /**
     * Returns the x position of the chunk
     *
     * @return The x position
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the z position of the chunk
     *
     * @return The z position
     */
    public int getZ() {
        return z;
    }

    /**
     * Returns the section number of the chunk
     *
     * @return The section number
     */
    public int getSectionNumber() {
        return sectionNumber;
    }

    /**
     * Returns this build's build number
     *
     * @return The build number
     */
    public int getBuildNumber() {
        return buildNumber;
    }

    /**
     * Returns the data (built model)
     *
     * @return The data
     */
    public TUint8Array getData() {
        return data;
    }

    public TUint8Array getTransData() {
        return transData;
    }

    public JsArray<PositionedModel> getTrans() {
        return modelJsArray;
    }

    public JsArrayInteger getAccessData() {
        return accessData;
    }

    @Override
    public void serialize(WriteSerializer serializer) {
        super.serialize(serializer);
        serializer.putInt("x", x);
        serializer.putInt("z", z);
        serializer.putInt("sectionNumber", sectionNumber);
        serializer.putInt("buildNumber", buildNumber);
        serializer.putTemp("accessData", accessData);
        serializer.putTemp("data", data);
        serializer.putTemp("transData", transData);
        serializer.putTemp("trans", modelJsArray);
    }

    @Override
    protected void read(ReadSerializer serializer) {
        x = serializer.getInt("x");
        z = serializer.getInt("z");
        sectionNumber = serializer.getInt("sectionNumber");
        buildNumber = serializer.getInt("buildNumber");
        accessData = (JsArrayInteger) serializer.getTemp("accessData");
        data = (TUint8Array) serializer.getTemp("data");
        transData = (TUint8Array) serializer.getTemp("transData");
        modelJsArray = (JsArray<PositionedModel>) serializer.getTemp("trans");
    }

    @Override
    protected WorkerMessage create() {
        return new ChunkBuildReply();
    }

    @Override
    public void handle(MessageHandler handler) {
        handler.handle(this);
    }
}
