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

package uk.co.thinkofdeath.thinkcraft.shared.platform.buffers;

/**
 * Unaligned access to buffers
 */
public interface ViewBuffer {

    void setUInt8(int index, int value);

    void setInt8(int index, int value);

    void setUInt16(int index, int value);

    void setInt16(int index, int value);

    void setUInt32(int index, int value);

    void setInt32(int index, int value);

    void setFloat32(int index, float value);

    void setFloat64(int index, double value);

    int getUInt8(int index);

    int getInt8(int index);

    int getUInt16(int index);

    int getInt16(int index);

    int getUInt32(int index);

    int getInt32(int index);

    float getFloat32(int index);

    double getFloat64(int index);
}
