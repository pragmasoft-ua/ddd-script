package ua.pragmasoft.ddd.domain.script;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import ua.pragmasoft.ddd.domain.script.ScriptInfo.Status;

class ScriptServiceImpl implements ScriptService {

    final ScriptInfoRepository repository;
    final ExecutorService executor;
    final ScriptFactory scriptFactory;

    public ScriptServiceImpl(ScriptInfoRepository repository, ScriptFactory scriptFactory, ExecutorService executor) {
        this.repository = repository;
        this.executor = executor;
        this.scriptFactory = scriptFactory;
    }

    @Override
    public void store(ScriptInfo s) throws ScriptException {
        this.repository.store(s);
    }

    @Override
    public void delete(ScriptInfo s) throws ScriptException {
        this.repository.delete(s);
    }

    @Override
    public ScriptInfo get(String name) throws ScriptException {
        return this.repository.get(name);
    }

    @Override
    public Iterable<ScriptInfo> all(SortBy by) throws ScriptException {
        return this.repository.all(by);
    }

    @Override
    public ScriptInfo create(String code, String name) throws ScriptException {
        var out = new ScriptOutputImpl();
        var err = new ScriptOutputImpl();
        var script = scriptFactory.createScript(code, out.asStream(), err.asStream());
        var scriptInfo = new ScriptInfo(script, name, out, err);
        this.repository.store(scriptInfo);
        return scriptInfo;
    }

    @Override
    public Future<Status> execute(ScriptInfo scriptInfo) throws ScriptException {
        this.executor.submit(scriptInfo.execution);
        return scriptInfo.execution;
    }

}