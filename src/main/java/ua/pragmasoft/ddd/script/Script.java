package ua.pragmasoft.ddd.script;

import java.io.OutputStream;

/**
 * Anticorruption layer around GraalJS parsed Value
 */
public abstract class Script implements Runnable, AutoCloseable {

    public final OutputStream out;
    public final OutputStream err;

    protected Script(OutputStream out, OutputStream err) {
        this.out = out;
        this.err = err;
    }

}
