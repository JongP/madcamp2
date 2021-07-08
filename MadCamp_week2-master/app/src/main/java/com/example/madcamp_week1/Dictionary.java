package com.example.madcamp_week1;

public class Dictionary {
    private String index;
    private String name;
    private String contact;

    public Dictionary(String index, String name, String contact) {
        this.index = index;
        this.name = name;
        this.contact = contact;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
