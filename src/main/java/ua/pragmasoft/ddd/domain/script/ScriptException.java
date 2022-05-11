package ua.pragmasoft.ddd.domain.script;

/**
 * Abstract base class for domain exceptions
 */
public abstract class ScriptException extends RuntimeException {

    ScriptException() {
    }

    ScriptException(String arg0) {
        super(arg0);
    }

    ScriptException(Throwable arg0) {
        super(arg0);
    }

    ScriptException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    ScriptException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }

}
