package com.example.vidish.words;

/**
 * Created by Vidish on 16-06-2018.
 */

class WordMeaningObject
{
    String definitions[], lexicalCategory, audio;

    public WordMeaningObject(){}
    public WordMeaningObject(String[] definitions, String lexicalCategory, String audio)
    {
        this.definitions = definitions;
        this.lexicalCategory = lexicalCategory;
        this.audio = audio;
    }

    public String[] getDefinitions() {
        return definitions;
    }

    public String getLexicalCategory() {
        return lexicalCategory;
    }

    public String getAudio() {
        return audio;
    }
}
