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

package uk.co.thinkofdeath.mapviewer.shared.building;

import elemental.html.ArrayBuffer;
import uk.co.thinkofdeath.mapviewer.shared.support.TUint8Array;

import java.util.ArrayList;

public class BufferPool {

    private ArrayList<ArrayBuffer> freeBuffers = new ArrayList<>();

    public TUint8Array alloc(int size) {
        for (int i = 0, freeBuffersSize = freeBuffers.size(); i < freeBuffersSize; i++) {
            ArrayBuffer buffer = freeBuffers.get(i);
            if (buffer.getByteLength() >= size) {
                freeBuffers.remove(i);
                return TUint8Array.create(buffer, 0, size);
            }
        }
        return TUint8Array.create(size);
    }

    public TUint8Array resize(TUint8Array old, int size) {
        if (old.getBuffer().getByteLength() >= size) {
            return TUint8Array.create(old.getBuffer(), 0, size);
        }
        TUint8Array nBuf = alloc(size);
        nBuf.set(old);
        free(old);
        return nBuf;
    }

    public void free(TUint8Array arr) {
        freeBuffers.add(arr.getBuffer());
    }
}
