package ua.pragmasoft.ddd.script;

import java.io.OutputStream;

public interface ScriptFactory {
    Script createScript(String sourceCode, OutputStream stdOut, OutputStream stdErr) throws ScriptException;

    @SuppressWarnings("java:S106") // to simplify testing
    default Script createScript(String sourceCode) throws ScriptException {
        return createScript(sourceCode, System.out, System.err);
    }

}
