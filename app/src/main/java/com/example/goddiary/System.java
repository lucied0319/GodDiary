package com.example.goddiary;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class System extends RealmObject {
    @PrimaryKey
    public int id;
    public String tag;
    public byte[] mainImage;

}
