package ua.pragmasoft.ddd.domain.script;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class ScriptInfo {

    static final Logger log = Logger.getLogger(ScriptInfo.class.getName());

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

    ScriptInfo(Script script, String name, ScriptOutput out, ScriptOutput err) {
        this.script = script;
        this.name = name;
        this.status = new AtomicReference<>(Status.NEW);
        this.out = out;
        this.err = err;
        this.created = Instant.now();
        this.execution = new FutureTask<>(this::callScript);
    }

    public enum Status {
        NEW, RUNNING, COMPLETED, ERROR
    }

    String getOut() {
        return out.toString();
    }

    String getErr() {
        return err.toString();
    }

    protected final void setStatus(Status expectedStatus, Status newStatus) {
        boolean success = this.status.compareAndSet(expectedStatus, newStatus);
        if (!success) {
            throw new IllegalStateException(
                    "Expectation failed: " + expectedStatus + " got instead " + getStatus());
        }
        this.observable.firePropertyChange("status", expectedStatus, status);
    }

    public Status getStatus() {
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

    public void stopExecution() {
        this.execution.cancel(true);
    }

    protected Status callScript() {
        try {
            this.started = Instant.now();
            setStatus(Status.NEW, Status.RUNNING);
            script.run();
            setStatus(Status.RUNNING, Status.COMPLETED);
        } catch (Exception e) {
            log.throwing(ScriptInfo.class.getSimpleName(), "callScript", e);
            setStatus(Status.RUNNING, Status.ERROR);
            try (var errorPrintStream = new PrintStream(this.err.asStream())) {
                errorPrintStream.println("Exception: " + e.getClass() + " " + e.getMessage());
                e.printStackTrace(errorPrintStream);
            }
        } finally {
            this.finished = Instant.now();
        }
        log.fine(this::toString);
        return getStatus();
    }

    @Override
    public String toString() {
        return "Script [name=" + name + ", status=" + status + "]";
    }

    public interface ScriptOutput {

        OutputStream asStream();

        @Override
        String toString();
    }

}
