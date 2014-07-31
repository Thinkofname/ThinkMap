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

package uk.co.thinkofdeath.thinkcraft.html.shared;

import uk.co.thinkofdeath.thinkcraft.shared.platform.Platform;

public class JavascriptLib {

    private JavascriptLib() {

    }

    /**
     * Sets up the native lib overrides and features
     */
    public static void init() {
        System.setOut(new JavascriptConsolePrinter(false));
        System.setErr(new JavascriptConsolePrinter(true));
        Platform.setPlatform(new JavascriptPlatform());
    }
}
