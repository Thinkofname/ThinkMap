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

import uk.co.thinkofdeath.thinkcraft.shared.model.PositionedModel;
import uk.co.thinkofdeath.thinkcraft.shared.platform.Platform;
import uk.co.thinkofdeath.thinkcraft.shared.platform.buffers.UByteBuffer;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.IntArraySerializer;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.Serializer;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.SerializerArraySerializer;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.StringArraySerializer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ChunkBuildReply extends WorkerMessage {
    private int x;
    private int z;
    private int sectionNumber;
    private int buildNumber;
    private int[] accessData;
    private UByteBuffer data;
    private UByteBuffer transData;
    private List<PositionedModel> models;
    private List<String> usedTextures = new ArrayList<>();

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
     * @param models
     * @param usedTextures
     */
    public ChunkBuildReply(int x, int z, int sectionNumber, int buildNumber, int[] accessData,
                           UByteBuffer data, UByteBuffer transData, List<PositionedModel> models, HashSet<String> usedTextures) {
        this.x = x;
        this.z = z;
        this.sectionNumber = sectionNumber;
        this.buildNumber = buildNumber;
        this.accessData = accessData;
        this.data = data;
        this.transData = transData;
        this.models = models;
        this.usedTextures.addAll(usedTextures);
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
    public UByteBuffer getData() {
        return data;
    }

    public UByteBuffer getTransData() {
        return transData;
    }

    public List<PositionedModel> getTrans() {
        return models;
    }

    public int[] getAccessData() {
        return accessData;
    }

    public List<String> getUsedTextures() {
        return usedTextures;
    }

    @Override
    public void serialize(Serializer serializer) {
        super.serialize(serializer);
        serializer.putInt("x", x);
        serializer.putInt("z", z);
        serializer.putInt("sectionNumber", sectionNumber);
        serializer.putInt("buildNumber", buildNumber);
        IntArraySerializer ad = Platform.workerSerializers().createIntArray();
        for (int i : accessData) {
            ad.add(i);
        }
        serializer.putArray("accessData", ad);
        serializer.putBuffer("data", data);
        serializer.putBuffer("transData", transData);

        SerializerArraySerializer t = Platform.workerSerializers().createSerializerArray();
        for (PositionedModel model : models) {
            Serializer m = Platform.workerSerializers().create();
            model.serialize(m);
            t.add(m);
        }
        serializer.putArray("models", t);

        StringArraySerializer ut = Platform.workerSerializers().createStringArray();
        for (String usedTexture : usedTextures) {
            ut.add(usedTexture);
        }
        serializer.putArray("usedTextures", ut);
    }

    @Override
    public void deserialize(Serializer serializer) {
        super.deserialize(serializer);
        x = serializer.getInt("x");
        z = serializer.getInt("z");
        sectionNumber = serializer.getInt("sectionNumber");
        buildNumber = serializer.getInt("buildNumber");
        IntArraySerializer ad = (IntArraySerializer) serializer.getArray("accessData");
        accessData = new int[ad.size()];
        for (int i = 0; i < accessData.length; i++) {
            accessData[i] = ad.getInt(i);
        }
        data = (UByteBuffer) serializer.getBuffer("data");
        transData = (UByteBuffer) serializer.getBuffer("transData");

        models = new ArrayList<>();
        SerializerArraySerializer t = (SerializerArraySerializer) serializer.getArray("models");
        for (int i = 0; i < t.size(); i++) {
            PositionedModel model = new PositionedModel();
            model.deserialize(t.get(i));
            models.add(model);
        }

        StringArraySerializer ut = (StringArraySerializer) serializer.getArray("usedTextures");
        for (int i = 0; i < ut.size(); i++) {
            usedTextures.add(ut.get(i));
        }
    }

    @Override
    public ChunkBuildReply create() {
        return new ChunkBuildReply();
    }

    @Override
    public void handle(MessageHandler handler) {
        handler.handle(this);
    }
}
