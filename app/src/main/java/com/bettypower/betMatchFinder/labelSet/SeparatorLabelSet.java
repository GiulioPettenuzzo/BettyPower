package com.bettypower.betMatchFinder.labelSet;

import java.util.ArrayList;

/**
 * Created by giuliopettenuzzo on 02/11/17.
 */

public class SeparatorLabelSet {

    public ArrayList<String> getAllQuoteSeparator(){
        ArrayList<String> quoteSeparator = new ArrayList<>();
        quoteSeparator.add(".");
        quoteSeparator.add(",");
        //TODO i ':'
        return quoteSeparator;
    }

    public ArrayList<String> getAllPalimpsestSeparator(){
        ArrayList<String> result = new ArrayList<>();
        result.add("-");
        result.add(".");
        result.add(":");
        result.add("_");
        result.add("/");
        result.add("|");
        result.add("\\");
        result.add("'");
        result.add("~");
        result.add(",");
        return result;
    }

    public ArrayList<String> getAllDateSeparator(){
        ArrayList<String> result = new ArrayList<>();
        result.add("/");
        result.add("\\");
        result.add("-");
        result.add(".");
        result.add("|");
        result.add("1");
        result.add("7");
        return result;
    }

    public ArrayList<String> getAllHourSeparator(){
        ArrayList<String> result = new ArrayList<>();
        result.add(":");
        result.add(".");
        result.add(",");
        return result;
    }

    public  ArrayList<String> getAllNameSeparator(){
        ArrayList<String> result = new ArrayList<>();
        result.add("-");
        result.add("~");
        result.add(".");
        return result;
    }

    public  ArrayList<String> getMoneySeparator(){
        ArrayList<String> result = new ArrayList<>();
        result.add(".");
        result.add(",");
        return result;
    }
}
