package persistence.repository;

import model.Entity;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AbstractRepository <T extends Entity<ID>, ID> implements Repository<T, ID> {

    protected Map<ID,T> elem;

    public AbstractRepository(){
        elem= new HashMap<>();

    }
    public void add(T el) throws SQLException {
        if(elem.containsKey(el.getID()))
            throw new RuntimeException("Element already exists!!!");
        else
            elem.put(el.getID(),el);
    }

    public void delete(T el){
        if(elem.containsKey(el.getID()))
            elem.remove(el.getID());
    }

    public void update(T el,ID id){
        if(elem.containsKey(id))
            elem.put(el.getID(),el);
        else
            throw new RuntimeException("Element doesn’t exist");
    }


    public T findById( ID id){
        if(elem.containsKey(id))
        return elem.get(id);
        else
            throw new RuntimeException("Element doesn't exist");
    }
    public Iterable<T> findAll() throws SQLException {
        return elem.values();
    }
}


