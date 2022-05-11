package ua.pragmasoft.ddd.script;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.OutputStream;
import java.io.PrintStream;
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

    ScriptInfo(Script script, String name, Instant created, ScriptOutput out, ScriptOutput err) {
        this.script = script;
        this.name = name;
        this.status = new AtomicReference<>(Status.SCHEDULED);
        this.out = out;
        this.err = err;
        this.created = created;
        this.execution = new FutureTask<>(this::callScript);
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

    protected final synchronized void setStatus(Status expectedStatus, Status newStatus) {
        boolean success = this.status.compareAndSet(expectedStatus, newStatus);
        if (!success) {
            throw new IllegalStateException(
                    "Expectation failed: " + expectedStatus + " got instead " + getStatus());
        }
        this.observable.firePropertyChange("status", expectedStatus, status);
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

    protected Status callScript() {
        try {
            this.started = Instant.now();
            setStatus(Status.SCHEDULED, Status.RUNNING);
            script.run();
            setStatus(Status.RUNNING, Status.COMPLETED);
        } catch (Exception e) {
            setStatus(Status.RUNNING, Status.ERROR);
            try (var errorPrintStream = new PrintStream(this.err.asStream())) {
                errorPrintStream.println("Exception: " + e.getClass() + " " + e.getMessage());
                e.printStackTrace(errorPrintStream);
            }
        } finally {
            this.finished = Instant.now();
        }
        return getStatus();
    }
}
