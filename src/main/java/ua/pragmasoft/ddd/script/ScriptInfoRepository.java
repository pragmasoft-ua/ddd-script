package ua.pragmasoft.ddd.script;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface ScriptInfoRepository {

    void store(ScriptInfo s) throws ScriptException;

    void delete(ScriptInfo s) throws ScriptException;

    ScriptInfo get(String name) throws ScriptException;

    Iterable<ScriptInfo> all(SortBy by) throws ScriptException;

    default Stream<ScriptInfo> allAsStream(SortBy by, boolean parallel) throws ScriptException {
        return StreamSupport.stream(all(by).spliterator(), parallel);
    }

    default Iterable<ScriptInfo> all() throws ScriptException {
        return all(SortBy.CREATED);
    }

    public enum SortBy {
        NAME, STATUS, CREATED
    }
}
