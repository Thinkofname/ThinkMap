package uk.co.thinkofdeath.mapviewer.linker;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.dev.util.DefaultTextOutput;

import java.util.Set;


@LinkerOrder(LinkerOrder.Order.PRIMARY)
public class WorkerLinker extends AbstractLinker {
    @Override
    public String getDescription() {
        return "ThinkMap - WebWorker";
    }

    @Override
    public ArtifactSet link(TreeLogger logger, LinkerContext context, ArtifactSet artifacts)
            throws UnableToCompleteException {
        ArtifactSet set = new ArtifactSet(artifacts);
        DefaultTextOutput output = new DefaultTextOutput(true);

        Set<CompilationResult> results = artifacts.find(CompilationResult.class);
        CompilationResult result = results.iterator().next();

        output.print("(function() {");
        output.indentOut();
        output.newline();
        output.print("var $wnd = self, $doc = {compatMode:false}," +
                "$stats = function(){}, $sessionId = function(){};");
        output.newline();
        output.print(result.getJavaScript()[0]);
        output.newline();
        output.print("gwtOnLoad(null, '" + context.getModuleName() + "', null);");
        output.indentIn();
        output.newline();
        output.print("})();");

        set.add(emitString(logger, output.toString(), context.getModuleName() + ".js"));
        return set;
    }
}
