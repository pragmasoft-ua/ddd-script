package ua.pragmasoft.ddd.script;

import java.util.UUID;
import java.util.concurrent.Future;

public interface ScriptService extends ScriptInfoRepository {

    ScriptInfo create(String code, String name) throws ScriptException;

    default ScriptInfo create(String code) throws ScriptException {
        return create(code, UUID.randomUUID().toString());
    }

    Future<ScriptInfo.Status> execute(ScriptInfo scriptInfo) throws ScriptException;

    default Future<ScriptInfo.Status> executionOf(ScriptInfo scriptInfo) {
        return scriptInfo.execution;
    };
}
