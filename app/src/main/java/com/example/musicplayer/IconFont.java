package com.example.musicplayer;


public  enum IconFont{
    PRE("\ue6ac"),
    NEXT("\ue6a9"),
    PLAY("\ue6ad"),
    STOP("\ue6ae");

    IconFont(String value) {
        this.value = value;
    }
    private String value;
    public String getValue(){
        return value;
    }
}

