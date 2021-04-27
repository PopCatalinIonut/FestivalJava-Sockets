package model;

import java.io.Serializable;


public class User implements Comparable<User>, Serializable,Entity<String>{
    private String username, passwd;


    public User() {
        this("");
    }

    public User(String username) {
        this(username,"");
    }
    public User(String username, String passwd) {
        this.username = username;
        this.passwd = passwd;
    }

    public String getPasswd() {
        return passwd;
    }

    public String getId() { return username; }


    public void setId(String id) {
        username = id;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }


    public int compareTo(User o) {
        return username.compareTo(o.username);
    }

    public boolean equals(Object obj) {
        if (obj instanceof User){
            User u=(User)obj;
            return username.equals(u.username);
        }
        return false;
    }

    @Override
    public String toString() {
     /*  StringBuilder friendsString=new StringBuilder();
        for (User user : friends){
            friendsString.append(user.getName());
            friendsString.append(", ");
        }*/
        return "User{" +
                "username='" + username + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }

    @Override
    public String getID() {
        return this.getId();
    }

    @Override
    public void setID(String id) {
    this.setId(id);
    }
}
