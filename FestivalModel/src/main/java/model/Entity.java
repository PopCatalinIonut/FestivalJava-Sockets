package model;

public interface Entity<Tid> {
    Tid getID();
    void setID(Tid id);
}
