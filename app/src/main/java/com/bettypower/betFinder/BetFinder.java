package com.bettypower.betFinder;

import com.bettypower.entities.Match;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 13/10/17.
 */

public class BetFinder {

    private String[] arrayOcrText;
    private ArrayList<Match> allMatchFound;

    public BetFinder(String ocrText, ArrayList<Match> allMatchFound){
        this.allMatchFound = allMatchFound;
        StringTokenizer token = new StringTokenizer(ocrText);
        int length = token.countTokens();
        arrayOcrText = new String[length];
        int count = 0;
        while (token.hasMoreTokens()){
            String word = token.nextToken();
            arrayOcrText[count] = word;
        }
    }

    private ArrayList<Match> getAllMatchWithBet(){


        return allMatchFound;
    }

    private boolean isQuote(String word){
        if(word.length() == 4){
            if(isNumber(String.valueOf(word.charAt(0))) && (word.charAt(1)==',' || word.charAt(1)=='.') &&
                    isNumber(String.valueOf(word.charAt(2))) && isNumber(String.valueOf(word.charAt(3)))){
                return true;
            }
        }
        return false;
    }

    private boolean isBet(String word){
        AllBetsAllowed allBet = new AllBetsAllowed();
        for (String currentBet:allBet.getAllBetAllowed()
             ) {
            if(currentBet.equalsIgnoreCase(word)){
                return true;
            }
        }
        return false;
    }

    private boolean isNumber(String word){
        try{
            int a = Integer.parseInt(word);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
}
