package ua.pragmasoft.ddd.script;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.FutureTask;

public class ScriptExecution extends FutureTask<ScriptInfo.Status> implements PropertyChangeListener {

    protected final ScriptInfo scriptInfo;
    Instant started = null;
    Instant finished = null;

    public ScriptExecution(ScriptInfo scriptInfo) {
        super(scriptInfo);
        this.scriptInfo = scriptInfo;
        this.scriptInfo.addPropertyChangeListener(this);
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

    String getName() {
        return scriptInfo.name;
    }

    ScriptInfo.Status getStatus() {
        return scriptInfo.getStatus();
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getNewValue() == ScriptInfo.Status.RUNNING) {
            this.started = Instant.now();
        } else {
            this.finished = Instant.now();
        }
    }
}
