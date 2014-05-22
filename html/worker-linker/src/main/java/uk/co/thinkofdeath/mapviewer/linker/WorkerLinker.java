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
import com.google.gwt.core.ext.linker.*;
import com.google.gwt.core.ext.linker.impl.SelectionScriptLinker;
import com.google.gwt.dev.util.DefaultTextOutput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@LinkerOrder(LinkerOrder.Order.PRIMARY)
public class WorkerLinker extends SelectionScriptLinker {

    @Override
    protected Collection<Artifact<?>> doEmitCompilation(TreeLogger logger, LinkerContext context, CompilationResult result, ArtifactSet artifacts) throws UnableToCompleteException {
        String[] javascript = result.getJavaScript();
        if (javascript.length != 1) {
            logger.branch(TreeLogger.ERROR, "Too many fragments");
            throw new UnableToCompleteException();
        }
        ArrayList<Artifact<?>> newArtifacts = new ArrayList<>();
        newArtifacts.addAll(emitSelectionInformation(result.getStrongName(), result));
        newArtifacts.add(new WorkerScript(result.getStrongName(), javascript[0]));
        return newArtifacts;
    }

    @Override
    protected EmittedArtifact emitSelectionScript(TreeLogger logger, LinkerContext context, ArtifactSet artifacts) throws UnableToCompleteException {
        Set<WorkerScript> scripts = artifacts.find(WorkerScript.class);
        if (scripts.size() != 1) {
            logger.branch(TreeLogger.ERROR, "Too many scripts");
            throw new UnableToCompleteException();
        }
        WorkerScript script = scripts.iterator().next();

        DefaultTextOutput output = new DefaultTextOutput(true);

        output.print(context.optimizeJavaScript(logger, generateSelectionScript(logger, context, artifacts)));
        output.newlineOpt();
        output.print(script.javascript);
        output.newlineOpt();
        output.print("gwtOnLoad(null, \"__MODULE_NAME__\", null);");

        return emitString(logger, output.toString(), context.getModuleName() + ".worker.js");
    }

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

    @Transferable
    private class WorkerScript extends Artifact<WorkerScript> {
        private final String strongName;
        private final String javascript;

        public WorkerScript(String strongName, String javascript) {
            super(WorkerLinker.class);
            this.strongName = strongName;
            this.javascript = javascript;
        }

        @Override
        public int hashCode() {
            int result = strongName.hashCode();
            result = 31 * result + javascript.hashCode();
            return result;
        }

        @Override
        protected int compareToComparableArtifact(WorkerScript workerScript) {
            int val = strongName.compareTo(workerScript.strongName);
            if (val == 0) {
                return javascript.compareTo(workerScript.javascript);
            }
            return val;
        }

        @Override
        protected Class<WorkerScript> getComparableArtifactType() {
            return WorkerScript.class;
        }
    }
}
