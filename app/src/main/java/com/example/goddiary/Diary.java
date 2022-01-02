package com.example.goddiary;

import android.app.Application;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Diary extends RealmObject {
    @PrimaryKey
    public String date;
    public int year;
    public int month;
    public int weather;
    public int tag;
    public int condition;
    public String title;
    public String bodyText;
    public byte[] image;


}
