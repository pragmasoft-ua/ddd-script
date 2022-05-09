package ua.pragmasoft.ddd.script;

import java.util.Optional;
import java.util.UUID;

public interface ScriptService extends ScriptInfoRepository {

    ScriptInfo create(String code, String name) throws ScriptException;

    default ScriptInfo create(String code) throws ScriptException {
        return create(code, UUID.randomUUID().toString());
    };

    ScriptExecution execute(ScriptInfo scriptInfo) throws ScriptException;

    Optional<ScriptExecution> executionOf(ScriptInfo scriptInfo) throws ScriptException;
}
