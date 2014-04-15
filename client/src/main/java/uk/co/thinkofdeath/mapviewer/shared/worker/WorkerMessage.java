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

public class WorkerMessage extends JavaScriptObject {

    protected WorkerMessage() {
    }

    /**
     * Creates a worker message which can be sent between a worker and a browser
     *
     * @param type
     *         Type of message
     * @param msg
     *         The actual message
     * @param ret
     *         Whether the worker/browser can reply
     * @return The created message
     */
    public static native WorkerMessage create(String type, Object msg, boolean ret)/*-{
        return {type: type, msg: msg, ret: ret};
    }-*/;

    /**
     * Returns the type of this message
     *
     * @return Type of the message
     */
    public final native String getType()/*-{
        return this.type;
    }-*/;

    /**
     * Returns the message
     *
     * @return The message
     */
    public final native Object getMessage()/*-{
        return this.msg;
    }-*/;

    /**
     * Returns whether this message should be replied to
     *
     * @return Whether this message should be replied to
     */
    public final native boolean getReturn()/*-{
        return this.ret;
    }-*/;
}
