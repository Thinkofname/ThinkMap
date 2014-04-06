package uk.co.thinkofdeath.mapviewer.shared.support;

import com.google.gwt.core.client.JavaScriptObject;
import elemental.html.ArrayBuffer;

// Because GWT's version is wrong
public class DataReader extends JavaScriptObject {

    protected DataReader() {}

    public static final native DataReader create(ArrayBuffer buffer)/*-{
        return new DataView(buffer);
    }-*/;

    public static final native DataReader create(ArrayBuffer buffer, int offset)/*-{
        return new DataView(buffer, offset);
    }-*/;

    public final native int getUint8(int offset)/*-{
        return this.getUint8(offset);
    }-*/;
}
