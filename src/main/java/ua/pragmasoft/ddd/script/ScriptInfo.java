package ua.pragmasoft.ddd.script;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

public class ScriptInfo {

    public final Script script;
    public final String name;
    protected final AtomicReference<Status> status;
    public final Instant created;
    private volatile Instant started = null;
    private volatile Instant finished = null;
    private final ScriptOutput out;
    private final ScriptOutput err;
    final FutureTask<ScriptInfo.Status> execution;
    private final PropertyChangeSupport observable = new PropertyChangeSupport(this);

    ScriptInfo(Script script, String name, Status status, Instant created, ScriptOutput out, ScriptOutput err) {
        this.script = script;
        this.name = name;
        this.status = new AtomicReference<>(status);
        this.out = out;
        this.err = err;
        this.created = created;
        this.execution = new FutureTask<>(this::call);
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

    protected final synchronized void setStatus(Status status) {
        // ensure status invariants
        // then use CompareAndSet
        Status old = this.status.getAndSet(status);
        this.observable.firePropertyChange("status", old, status);
    }

    public synchronized Status getStatus() {
        return status.get();
    }

    Optional<Instant> getStarted() {
        return Optional.ofNullable(this.started);
    }

    Optional<Instant> getFinished() {
        return Optional.ofNullable(this.finished);
    }

    Optional<Duration> getDuration() {
        return Optional.ofNullable(this.started)
                .flatMap(s -> Optional.ofNullable(this.finished != null ? Duration.between(s, this.finished) : null));
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

    class ScriptOutputImpl implements ScriptOutput {

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

    protected Status call() {
        try {
            this.started = Instant.now();
            setStatus(Status.RUNNING);
            script.run();
            setStatus(Status.COMPLETED);
        } catch (Exception e) {
            setStatus(Status.ERROR);
            // handle error
        } finally {
            this.finished = Instant.now();
        }
        return getStatus();
    }
}
