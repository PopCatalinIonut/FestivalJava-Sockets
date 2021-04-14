package model;

import java.io.Serializable;

public class Artist implements Entity<Integer>, Serializable {

    private int ID;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Integer getID() {
        return this.ID;
    }

    public Artist(int ID){ this.ID=ID; }
    public Artist(int ID, String name) {
        this.ID = ID;
        this.name = name;
    }

    @Override
    public void setID(Integer id) {
        this.ID=id;
    }

    @Override
    public String toString() {
        return "Performer{" + "ID=" + ID + ", name='" + name + '\'' + '}';
    }
}
