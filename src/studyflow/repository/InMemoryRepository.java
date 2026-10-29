package studyflow.repository;

import studyflow.model.BaseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class InMemoryRepository<T extends BaseEntity> implements Repository<T, Long> {
    protected final Map<Long, T> storage = new HashMap<>();

    @Override
    public T save(T entity) {
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }

    @Override
    public int size() {
        return storage.size();
    }
}