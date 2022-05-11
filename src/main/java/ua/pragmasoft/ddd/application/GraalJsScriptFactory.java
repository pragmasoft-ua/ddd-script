package ua.pragmasoft.ddd.application;

import java.io.OutputStream;

import ua.pragmasoft.ddd.domain.script.Script;
import ua.pragmasoft.ddd.domain.script.ScriptException;
import ua.pragmasoft.ddd.domain.script.ScriptFactory;
import ua.pragmasoft.ddd.domain.script.SyntaxException;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

public class GraalJsScriptFactory implements ScriptFactory {

    static final String JS = "js";

    @Override
    public Script createScript(String sourceCode, OutputStream stdOut, OutputStream stdErr) throws ScriptException {
        try {
            final Context ctx = Context.newBuilder(JS)
                    .out(stdOut)
                    .err(stdErr)
                    .build();
            final Value parsed = ctx.parse(JS, sourceCode);
            if (!parsed.canExecute()) {
                throw new IllegalStateException("Not executable script");
            }
            return new GraalJsScript(parsed, stdOut, stdErr);

        } catch (Exception e) {
            throw new SyntaxException("Bad syntax", e);
        }

    }

    static final class GraalJsScript extends Script {

        private final Value parsed;

        protected GraalJsScript(Value parsed, OutputStream out, OutputStream err) {
            super(out, err);
            this.parsed = parsed;
        }

        @Override
        public void run() {
            this.parsed.executeVoid();
        }

        @Override
        public void close() throws Exception {
            this.parsed.getContext().close(true);
        }

    }
}
