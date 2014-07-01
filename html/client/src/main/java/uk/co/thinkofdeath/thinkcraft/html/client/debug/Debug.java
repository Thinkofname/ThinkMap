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

package uk.co.thinkofdeath.thinkcraft.html.client.debug;

import com.google.gwt.typedarrays.client.Float32ArrayNative;
import com.google.gwt.typedarrays.client.Uint8ArrayNative;
import elemental.html.ArrayBufferView;
import elemental.html.WebGLBuffer;
import elemental.html.WebGLRenderingContext;
import uk.co.thinkofdeath.thinkcraft.shared.vector.Matrix4;

import static elemental.html.WebGLRenderingContext.*;

public class Debug {

    private static final boolean enabled = false;
    private static boolean init = false;

    private static final int MAX_LINES = 5000;
    private static DebugShader shader;

    private static WebGLBuffer buffer;
    private static Uint8ArrayNative data = Uint8ArrayNative.create(MAX_LINES * (4 * 3 + 4) * 2);
    private static Float32ArrayNative floatView = Float32ArrayNative.create(data.buffer());
    private static int count = 0;

    public static boolean isEnabled() {
        return enabled;
    }

    public static void init(WebGLRenderingContext gl) {
        if (init || !enabled) {
            return;
        }
        init = true;
        shader = new DebugShader();
        shader.setup(gl);

        buffer = gl.createBuffer();
    }

    public static void render(WebGLRenderingContext gl, Matrix4 perspectiveMatrix, Matrix4 viewMatrix) {
        if (!enabled) return;
        shader.use();

        shader.setPerspectiveMatrix(perspectiveMatrix);
        shader.setViewMatrix(viewMatrix);

        gl.lineWidth(5);

        gl.bindBuffer(ARRAY_BUFFER, buffer);
        gl.bufferData(ARRAY_BUFFER, (ArrayBufferView) (Object) data/*Hacky workaround*/, DYNAMIC_DRAW);
        gl.vertexAttribPointer(shader.getPosition(), 3, FLOAT, false, 16, 0);
        gl.vertexAttribPointer(shader.getColour(), 4, UNSIGNED_BYTE, true, 16, 12);
        gl.drawArrays(LINES, 0, count * 2);

        gl.lineWidth(1);

        shader.disable();
        count = 0;
    }

    public static void drawLine(double x1, double y1, double z1,
                                double x2, double y2, double z2,
                                int r, int g, int b) {
        if (!enabled) return;
        int offset = count * (4 * 3 + 4) * 2;
        int offsetF = count * 4 * 2;
        floatView.set(offsetF, (float) x1);
        floatView.set(offsetF + 1, (float) y1);
        floatView.set(offsetF + 2, (float) z1);
        data.set(offset + 12, r);
        data.set(offset + 13, g);
        data.set(offset + 14, b);
        data.set(offset + 15, 255);

        floatView.set(offsetF + 4, (float) x2);
        floatView.set(offsetF + 1 + 4, (float) y2);
        floatView.set(offsetF + 2 + 4, (float) z2);
        data.set(offset + 12 + 16, r);
        data.set(offset + 13 + 16, g);
        data.set(offset + 14 + 16, b);
        data.set(offset + 15 + 16, 255);
        count++;
    }

    public static void drawBox(double x1, double y1, double z1,
                               double x2, double y2, double z2,
                               int r, int g, int b) {
        drawLine(x1, y1, z1, x2, y1, z1, r, g, b);
        drawLine(x1, y1, z1, x1, y1, z2, r, g, b);
        drawLine(x2, y1, z2, x2, y1, z1, r, g, b);
        drawLine(x2, y1, z2, x1, y1, z2, r, g, b);

        drawLine(x1, y2, z1, x2, y2, z1, r, g, b);
        drawLine(x1, y2, z1, x1, y2, z2, r, g, b);
        drawLine(x2, y2, z2, x2, y2, z1, r, g, b);
        drawLine(x2, y2, z2, x1, y2, z2, r, g, b);

        drawLine(x1, y1, z1, x1, y2, z1, r, g, b);
        drawLine(x2, y1, z1, x2, y2, z1, r, g, b);
        drawLine(x2, y1, z2, x2, y2, z2, r, g, b);
        drawLine(x1, y1, z2, x1, y2, z2, r, g, b);
    }
}
