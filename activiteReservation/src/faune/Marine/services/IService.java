package faune.Marine.services;

import java.util.List;

public interface IService<T> {
    void add(T t);
    void update(T t);
    void delete(T t);
    T getOne(T t);
    List<T> getAll();
}