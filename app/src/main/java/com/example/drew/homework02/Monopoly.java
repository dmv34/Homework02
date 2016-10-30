package com.example.drew.homework02;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Drew VandeLune on 10/16/2016
 * This class provides a monopoly class for the main activity
 */
public class Monopoly {

    private String name, email;
    private int id;

    public Monopoly( int id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public int getID()
    {
        return id;
    }
    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

}
