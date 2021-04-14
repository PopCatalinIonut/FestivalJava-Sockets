package model;

import java.io.Serializable;

public class Ticket implements Entity<Integer>, Serializable {

    private int ID;
    private int festivalID;
    private String buyerName;
    private int seats;

    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public void setSeats(int seats) { this.seats = seats; }

    public String getBuyerName(){return this.buyerName;}

    public String toString() {
        return "Ticket{" + "ID=" + ID + ", festivalID=" + festivalID +
                ", buyerName='" + buyerName + '\'' +
                ", places=" + seats + '}';
    }

    public Ticket(int ID, int festivalID, String buyerName, int seats) {
        this.ID = ID;
        this.festivalID = festivalID;
        this.buyerName = buyerName;
        this.seats = seats;
    }

    public Ticket(int festivalID, String buyerName, int seats) {
        this.festivalID = festivalID;
        this.buyerName = buyerName;
        this.seats = seats;
    }

    public int getSeats() { return seats; }

    public int getFestivalID() {
        return festivalID;
    }

    public void setFestivalID(int festivalID) {
        this.festivalID = festivalID;
    }

    @Override
    public Integer getID() { return this.ID; }

    @Override
    public void setID(Integer id) { this.ID=id; }
}
