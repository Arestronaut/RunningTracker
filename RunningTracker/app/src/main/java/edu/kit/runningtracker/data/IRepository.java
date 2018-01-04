package edu.kit.runningtracker.data;

import java.util.Collection;

/**
 * Created by joshr on 19.12.2017.
 */

public interface IRepository<T> {
    void put(T element);
    T get(int id);
    Collection<T> get();
    void update(int id, T element);
    void delete(int id);
    void save();
    void clear();
}
