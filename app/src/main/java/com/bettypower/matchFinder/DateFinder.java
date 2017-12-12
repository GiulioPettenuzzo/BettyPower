package com.bettypower.matchFinder;

import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 15/08/17.
 */

public class DateFinder {

    private String text;
    private String timeDate;

    public DateFinder(String text, String timeDate){
        this.text = text;
        this.timeDate = timeDate;
    }

    private int findDate(){
        StringTokenizer token = new StringTokenizer(timeDate);
        String date = token.nextToken();
        String time = token.nextToken();
        boolean isDatePresent = findDate(date);
        boolean isTimePresent = findTime(time);
        return elaborateLikelyHood(isDatePresent,isTimePresent);
    }

    /**
     * caso in cui al posto di '/' legge '7'
     * @param date
     * @return
     */
    private String resolveDate(String date){
        try{
            if(date.equalsIgnoreCase("1270972017")){
                String c = "";
            }
            Long.parseLong(date);
            char[] charArray = date.toCharArray();
            if(charArray[2] == '7'){
                charArray[2] = '/';
            }
            if(charArray[5]== '7'){
                charArray[5] = '/';
            }
            date = String.valueOf(charArray);
            return date;
        }
        catch(Exception e){
            return date;
        }
    }

    /**
     * get the likelyhood of the date found correctness
     * @param isDatePresent
     * @param isTimePresent
     * @return 0 if the date is not found, 1 if only the time is present, 2 if only the date is present, 3 if date and time are present
     */
    private int elaborateLikelyHood(boolean isDatePresent, boolean isTimePresent){
        if(!isDatePresent && isTimePresent){
            return 1;
        }
        else if(isDatePresent && !isTimePresent){
            return 2;
        }
        else if(isDatePresent && isTimePresent){
            return 3;
        }
        else{
            return 0;
        }
    }
    //30/08 20:30 dal palinsesto
    //18/08/2017 20:16 dalla schedina se è 01 è scritto 1
    private boolean findDate(String date){
        StringTokenizer token = new StringTokenizer(text);
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            word = resolveDate(word);
            if(word.startsWith(date)){
                return true;
            }
            else{
                if(date.startsWith("0")){
                    word = "0"+word;
                    word = resolveDate(word);
                    if(word.startsWith(date)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @param time
     * @return true if the time is found, false otherwise
     */
    private boolean findTime(String time){
        StringTokenizer token = new StringTokenizer(text);
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            if(word.compareTo(time)==0){
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return 0 if the date were not found
     * 1 if match only the time
     * 2 if match only the date
     * 3 if match the time and the date
     */
    public int getLikelyHoodDate(){
        int likelyHood = findDate();
        return likelyHood;
    }
}
