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

package uk.co.thinkofdeath.mapviewer.linker;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.impl.SelectionScriptLinker;


@LinkerOrder(LinkerOrder.Order.PRIMARY)
public class WorkerLinker extends SelectionScriptLinker {
    @Override
    protected String getCompilationExtension(TreeLogger treeLogger, LinkerContext linkerContext) throws UnableToCompleteException {
        return ".worker.js";
    }

    @Override
    protected String getModulePrefix(TreeLogger treeLogger, LinkerContext linkerContext, String s) throws UnableToCompleteException {
        return "";
    }

    @Override
    protected String getModuleSuffix2(TreeLogger logger, LinkerContext context, String strongName) throws UnableToCompleteException {
        return "";
    }

    @Override
    protected String getSelectionScriptTemplate(TreeLogger treeLogger, LinkerContext linkerContext) throws UnableToCompleteException {
        return "uk/co/thinkofdeath/mapviewer/linker/Template.js";
    }

    @Override
    public String getDescription() {
        return "ThinkMap - WebWorker";
    }
}
