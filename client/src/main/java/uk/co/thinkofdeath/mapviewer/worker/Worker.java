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

package uk.co.thinkofdeath.mapviewer.worker;


import com.google.gwt.core.client.EntryPoint;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.MessageEvent;
import elemental.html.WorkerGlobalScope;

public class Worker implements EntryPoint, EventListener {

    @Override
    public void onModuleLoad() {
        setOnmessage(this);
    }


    public native WorkerGlobalScope getSelf()/*-{
        return $wnd;
    }-*/;

    public native void postMessage(Object message)/*-{
        $wnd.postMessage(message);
    }-*/;

    private native void setOnmessage(EventListener eventListener)/*-{
        self.onmessage = @elemental.js.dom.JsElementalMixinBase::getHandlerFor(Lelemental/events/EventListener;)(eventListener);
    }-*/;

    @Override
    public void handleEvent(Event evt) {
        MessageEvent event = (MessageEvent) evt;
    }
}
