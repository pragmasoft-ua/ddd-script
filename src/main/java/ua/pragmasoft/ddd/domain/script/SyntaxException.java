package ua.pragmasoft.ddd.domain.script;

public class SyntaxException extends ScriptException {

    public SyntaxException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public SyntaxException(String string) {
        super(string);
    }

}
