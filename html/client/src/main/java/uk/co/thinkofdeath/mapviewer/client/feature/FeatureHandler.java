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

package uk.co.thinkofdeath.mapviewer.client.feature;

import elemental.client.Browser;
import elemental.dom.Element;
import elemental.html.DivElement;
import elemental.html.ParagraphElement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FeatureHandler {

    private static final String WEBGL = "webgl";

    private static final Map<String, String> featureTitles = new HashMap<>();
    private static final Map<String, String> featureErrors = new HashMap<>();
    private final Set<String> missingFeatures = new HashSet<>();
    private boolean hasFailed = false;

    {
        featureTitles.put(WEBGL, "Missing or disabled WebGL");
        featureErrors.put(WEBGL, "Your browser currently doesn't support WebGL or it is disabled." +
                " Please visit <a href=\"http://get.webgl.org\">http://get.webgl.org</a> for more" +
                " information on obtaining a browser with WebGL support.");
    }

    /**
     * Checks if the required browser features are supported
     *
     * @return if the mapviewer can run on this browser
     */
    public boolean detect() {
        // Check for WebGL support
        if (checkWebGL()) {
            fail(WEBGL);
        }
        return handleMissing();
    }

    private boolean handleMissing() {
        if (hasFailed) {
            DivElement el = Browser.getDocument().createDivElement();
            el.getClassList().add("fade-background");
            Browser.getDocument().getBody().appendChild(el);

            DivElement message = Browser.getDocument().createDivElement();
            message.getClassList().add("center-message");
            Element title = Browser.getDocument().createElement("h1");
            title.setInnerHTML("Failed to start ThinkMap");
            message.appendChild(title);
            for (String feature : missingFeatures) {
                Element header = Browser.getDocument().createElement("h2");
                header.setInnerHTML(featureTitles.get(feature));
                message.appendChild(header);
                ParagraphElement body = Browser.getDocument().createParagraphElement();
                body.setInnerHTML(featureErrors.get(feature));
                message.appendChild(body);
            }
            Browser.getDocument().getBody().appendChild(message);
        }
        return !hasFailed;
    }

    private void fail(String feature) {
        missingFeatures.add(feature);
        hasFailed = true;
    }

    private native boolean checkWebGL()/*-{
        try {
            var c = $doc.createElement("canvas");
            var gl = c.getContext("webgl") || c.getContext("experimental-webgl");
            return gl == null;
        } catch (e) {
            return true;
        }
    }-*/;
}
