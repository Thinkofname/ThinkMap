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

package uk.co.thinkofdeath.thinkcraft.shared.model;

import uk.co.thinkofdeath.thinkcraft.shared.serializing.Serializable;
import uk.co.thinkofdeath.thinkcraft.shared.serializing.Serializer;

public class PositionedModel implements Serializable {
    private int x;
    private int y;
    private int z;
    private int start;
    private int length;

    public PositionedModel() {
    }

    public PositionedModel(int x, int y, int z, int start, int length) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.start = start;
        this.length = length;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }

    @Override
    public void serialize(Serializer serializer) {
        serializer.putInt("x", x);
        serializer.putInt("y", y);
        serializer.putInt("z", z);
        serializer.putInt("start", start);
        serializer.putInt("length", length);
    }

    @Override
    public void deserialize(Serializer serializer) {
        x = serializer.getInt("x");
        y = serializer.getInt("y");
        z = serializer.getInt("z");
        start = serializer.getInt("start");
        length = serializer.getInt("length");
    }

    @Override
    public Serializable create() {
        return new PositionedModel();
    }
}
