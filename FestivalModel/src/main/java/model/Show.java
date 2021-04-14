package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Show implements Entity<Integer> , Serializable {

    private int ID;
    private String name;
    private String location;
    private LocalDateTime date;
    private Artist artist;
    private int totalSeats;
    private int soldSeats;

    @Override
    public String toString() {
        return "Show{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", data=" + date +
                ", artist: " + artist.getName() +
                ", totalTickets=" + totalSeats +
                ", soldTickets=" + soldSeats +
                '}';
    }
    public Show(int ID, String name, String location, LocalDateTime date,Artist artist, int totalSeats, int soldSeats) {
        this.ID = ID;
        this.name = name;
        this.location = location;
        this.date = date;
        this.artist= artist;
        this.totalSeats = totalSeats;
        this.soldSeats = soldSeats;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setArtistID(Artist artist) {
        this.artist = artist;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public void setSoldSeats(int soldSeats) {
        this.soldSeats = soldSeats;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getDate() { return date; }

    public Artist getArtist() {
        return artist;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public int getSoldSeats() {
        return soldSeats;
    }

    @Override
    public Integer getID() {
        return this.ID;
    }

    @Override
    public void setID(Integer id) { this.ID=id; }

}
