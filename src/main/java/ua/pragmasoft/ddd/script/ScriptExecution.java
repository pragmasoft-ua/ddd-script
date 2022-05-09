package ua.pragmasoft.ddd.script;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.FutureTask;

public class ScriptExecution extends FutureTask<ScriptInfo> {

    public final ScriptInfo scriptInfo;
    Instant started = null;
    Instant finished = null;

    public ScriptExecution(ScriptInfo scriptInfo) {
        super(scriptInfo.script, scriptInfo);
        this.scriptInfo = scriptInfo;
    }

    @Override
    public void run() {
        try {
            this.started = Instant.now();
            super.run();
        } finally {
            this.finished = Instant.now();
        }
    }

    Optional<Instant> getStarted() {
        return Optional.ofNullable(this.started);
    }

    Optional<Instant> getFinished() {
        return Optional.ofNullable(this.finished);
    }

    Optional<Duration> getDuration() {
        return Optional.ofNullable(this.started)
                .flatMap((s) -> Optional.ofNullable(this.finished != null ? Duration.between(s, this.finished) : null));
    }
}
