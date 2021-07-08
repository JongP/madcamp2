package com.example.madcamp_week1;

import java.util.ArrayList;

public class Item {

    // common
    public int type;

    // if header
    public String category;
    public ArrayList<Item> invisibleChildren;

    // if child
    public Dictionary dict;

    public Item(String category){
        this.category = category;
        this.type = 0;
    }
    public Item(Dictionary dict) {
        this.dict = new Dictionary(dict.getIndex(), dict.getName(), dict.getContact());
        this.type = 1;
    }
}
