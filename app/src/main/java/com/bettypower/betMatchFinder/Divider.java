package com.bettypower.betMatchFinder;

import com.bettypower.betMatchFinder.labelSet.SeparatorLabelSet;

import java.util.ArrayList;

/**
 * This class recive a word from the finder and analize it in order to understnd what it could be
 * This reduce the time of execution
 * Created by giuliopettenuzzo on 02/11/17.
 */

public class Divider {

    private String word = "";

    private ArrayList<String> dateSeparatorLabelSet = new SeparatorLabelSet().getAllDateSeparator();
    private ArrayList<String> hourSeparatorLabelSet = new SeparatorLabelSet().getAllHourSeparator();
    private ArrayList<String> nameSeparatorLabelSet = new SeparatorLabelSet().getAllNameSeparator();
    private ArrayList<String> quoteSeparatorLabelSet = new SeparatorLabelSet().getAllQuoteSeparator();

    public static final String IS_POSSIBLE_NAME = "is_possible_name";
    public static final String IS_POSSIBLE_PALIMPSEST = "is_possible-palimpsest";
    public static final String IS_POSSIBLE_BET = "is_possible_bet";
    public static final String IS_POSSIBLE_QUOTE = "is_possible_quote";
    public static final String IS_POSSIBLE_HOUR = "is_possible_hour";
    public static final String IS_POSSIBLE_BOOKMAKER = "is_possible_bookmaker";
    public static final String IS_POSSIBLE_BETKIND = "is_possible_betkind";
    public static final String IS_POSSIBLE_DATE = "is_possible_date";

    public void setWord(String word){
        this.word = word;
    }

    public ArrayList<String> getPossibleFields(){
        ArrayList<String> fieldByLength = allFieldsByLength();
        ArrayList<String> fieldByNumber = allFieldsByNumbers();
        ArrayList<String> fieldBySeparator = allFieldsBySeparator();
        return intersection(fieldByLength,fieldByNumber,fieldBySeparator);
    }

    /***********************************************************************************************
     *********************************   PRIVATE METHODS   ****************************************
     **********************************************************************************************/

    private ArrayList<String> allFieldsByLength(){
        ArrayList<String> result = new ArrayList<>();

        result.add(IS_POSSIBLE_NAME);
        result.add(IS_POSSIBLE_BOOKMAKER);
        result.add(IS_POSSIBLE_BET);
        result.add(IS_POSSIBLE_BETKIND);

        if(word.length() > 5){
            result.add(IS_POSSIBLE_PALIMPSEST);
            result.add(IS_POSSIBLE_DATE);

        }
        else if(word.length()==5){
            result.add(IS_POSSIBLE_HOUR);
            result.add(IS_POSSIBLE_QUOTE);
            result.add(IS_POSSIBLE_PALIMPSEST);
            result.add(IS_POSSIBLE_DATE);

        }
        else{
            result.add(IS_POSSIBLE_HOUR);
            result.add(IS_POSSIBLE_QUOTE);
        }


        return result;
    }

    private ArrayList<String> allFieldsByNumbers(){
        ArrayList<String> result = new ArrayList<>();
        int numberCounter = 0;
        for(int i = 0;i<word.length();i++){
            if(isNumber(String.valueOf(word.charAt(i)))){
                numberCounter++;
            }
        }
        result.add(IS_POSSIBLE_BETKIND);
        result.add(IS_POSSIBLE_BET);
        result.add(IS_POSSIBLE_DATE);

        if(numberCounter>2){
            result.add(IS_POSSIBLE_QUOTE);
            result.add(IS_POSSIBLE_HOUR);
            result.add(IS_POSSIBLE_PALIMPSEST);
        }
        else{
            result.add(IS_POSSIBLE_NAME);
            result.add(IS_POSSIBLE_BOOKMAKER);
        }
        return result;
    }

    private ArrayList<String> allFieldsBySeparator(){
        ArrayList<String> result = new ArrayList<>();
        for (String currentLabelSet:hourSeparatorLabelSet
                ) {
            if(word.contains(currentLabelSet)){
                result.add(IS_POSSIBLE_HOUR);
            }
        }
        for (String currentLabelSet:quoteSeparatorLabelSet
                ) {
            if(word.contains(currentLabelSet)){
                result.add(IS_POSSIBLE_QUOTE);
            }
        }
        for (String currentLabelSet:dateSeparatorLabelSet
                ) {
            if(word.contains(currentLabelSet)){
                result.add(IS_POSSIBLE_DATE);
            }
        }
        if(result.size()==0){
            result.add(IS_POSSIBLE_BOOKMAKER); //-1 non va qui
            result.add(IS_POSSIBLE_NAME);
        }
        for (String currentLabelSet:nameSeparatorLabelSet
                ) {
            if(word.contains(currentLabelSet)){
                result.add(IS_POSSIBLE_NAME);
            }
        }
        result.add(IS_POSSIBLE_BET);
        result.add(IS_POSSIBLE_BETKIND);
        result.add(IS_POSSIBLE_PALIMPSEST);
        return result;
    }

    /***********************************************************************************************
     *********************************   AUSILIAR METHODS   ****************************************
     **********************************************************************************************/

    private ArrayList<String> intersection(ArrayList<String> listOne, ArrayList<String> listTwo, ArrayList<String> listThree){
        ArrayList<String> result = new ArrayList<>();
        for (String currentOne:listOne
                ) {
            for (String currentTwo:listTwo
                    ) {
                for (String currentThree:listThree
                        ) {
                    if(currentOne.equals(currentThree) && currentTwo.equals(currentOne) && result.lastIndexOf(currentOne)<0){
                        result.add(currentOne);
                    }
                }
            }
        }
        return result;
    }

    private boolean isNumber(String character){
        try{
            int number = Integer.parseInt(character);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

}
