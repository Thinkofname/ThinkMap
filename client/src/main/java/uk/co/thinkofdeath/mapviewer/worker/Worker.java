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
