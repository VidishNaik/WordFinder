package com.example.vidish.words;

/**
 * Created by Vidish on 01-03-2018.
 */

public class Letters {
    private String character;
    private int position;
    public Letters(){}
    public Letters(String s,int pos)
    {
        character = s;
        position = pos;
    }
    public String getCharacter() {
        return character;
    }
    public int getPosition() {
        return position;
    }
}
