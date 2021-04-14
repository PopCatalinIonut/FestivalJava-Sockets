package persistence.repository;

import java.sql.SQLException;

public interface Repository<T, Tid> {
    void add(T elem) throws SQLException;
    Iterable<T> findAll() throws SQLException;

}
