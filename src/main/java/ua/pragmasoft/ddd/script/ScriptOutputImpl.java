package ua.pragmasoft.ddd.script;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import ua.pragmasoft.ddd.script.ScriptInfo.ScriptOutput;

public class ScriptOutputImpl implements ScriptOutput {

    final ByteArrayOutputStream s = new ByteArrayOutputStream();

    @Override
    public OutputStream asStream() {
        return this.s;
    };

    @Override
    public String toString() {
        return s.toString();
    }
}