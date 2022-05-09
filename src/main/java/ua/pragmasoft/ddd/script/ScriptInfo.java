package ua.pragmasoft.ddd.script;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.OutputStream;
import java.time.Instant;

public class ScriptInfo {

    public final Script script;
    public final String name;
    protected Status status;
    public final Instant created;
    private final ScriptOutput out;
    private final ScriptOutput err;
    private final PropertyChangeSupport observable = new PropertyChangeSupport(this);

    ScriptInfo(Script script, String name, Status status, Instant created, ScriptOutput out, ScriptOutput err) {
        this.script = script;
        this.name = name;
        this.status = status;
        this.out = out;
        this.err = err;
        this.created = created;
    }

    public enum Status {
        INVALID, SCHEDULED, COMPLETED, RUNNING, ERROR
    }

    String getOut() {
        return out.toString();
    }

    String getErr() {
        return err.toString();
    }

    protected final void setStatus(Status status) {
        Status old = this.status;
        // ensure status invariants
        this.status = status;
        this.observable.firePropertyChange("status", old, status);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        observable.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        observable.removePropertyChangeListener(listener);
    }

    public interface ScriptOutput {

        OutputStream asStream();

        @Override
        String toString();
    }

}
