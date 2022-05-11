package ua.pragmasoft.ddd.application;

import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import ua.pragmasoft.ddd.domain.script.ConflictException;
import ua.pragmasoft.ddd.domain.script.NotFoundException;
import ua.pragmasoft.ddd.domain.script.ScriptException;
import ua.pragmasoft.ddd.domain.script.ScriptInfo;
import ua.pragmasoft.ddd.domain.script.ScriptInfoRepository;

public class InMemoryScriptInfoRepository implements ScriptInfoRepository {

    static final Map<SortBy, Comparator<ScriptInfo>> COMPARATORS = Map.of(
            SortBy.NAME,
            Comparator.comparing(InMemoryScriptInfoRepository::name),
            SortBy.STATUS,
            Comparator.comparing(ScriptInfo::getStatus),
            SortBy.CREATED,
            Comparator.comparing(InMemoryScriptInfoRepository::created));

    final Map<String, ScriptInfo> store = new ConcurrentHashMap<>(32);

    @Override
    public void store(ScriptInfo s) throws ScriptException {
        var existing = store.putIfAbsent(s.name, s);
        if (existing != null)
            throw new ConflictException("Already exists: " + s.name);
    }

    @Override
    public void delete(ScriptInfo s) throws ScriptException {
        var existing = store.remove(s.name, s);
        if (!existing)
            throw new NotFoundException("Does not exist: " + s.name);
    }

    @Override
    public ScriptInfo get(String name) throws ScriptException {
        return Optional.ofNullable(store.get(name)).orElseThrow(() -> new NotFoundException("Does not exist: " + name));
    }

    @Override
    public Iterable<ScriptInfo> all(SortBy by) throws ScriptException {
        return store.values().parallelStream().sorted(COMPARATORS.get(by)).toList();
    }

    private static String name(ScriptInfo i) {
        return i.name;
    }

    private static Instant created(ScriptInfo i) {
        return i.created;
    }

}
