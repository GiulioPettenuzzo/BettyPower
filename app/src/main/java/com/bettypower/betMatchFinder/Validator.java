package com.bettypower.betMatchFinder;

import com.bettypower.entities.PalimpsestMatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * this class is created in order to verify if the new element found by the ocr is consistency with the current
 * match we are elaborating
 * Created by giuliopettenuzzo on 21/10/17.
 */

//N.B.: IL VALIDATOR NON PU VERIFICARE CHE BET, BETKIND siano coerenti oppure no.
public class Validator {

    public static final String HOME_TEAM = "home_team";
    public static final String AWAY_TEAM = "away_team";

    public ArrayList<PalimpsestMatch> isPalimpsestValidate(String palimpsest, ArrayList<PalimpsestMatch> allPossibleMatches){
        ArrayList<PalimpsestMatch> result = new ArrayList<>();
        for (PalimpsestMatch currentMatch:allPossibleMatches
             ) {
            if(palimpsest.equals(currentMatch.getPalimpsest())){
                result.add(currentMatch);
                return result;
            }
        }
        return null;
    }
    
    public ArrayList<PalimpsestMatch> isDateValidate(String date,ArrayList<PalimpsestMatch> allPalimpsestMatch){
        ArrayList<PalimpsestMatch> result = new ArrayList<>();

        for (PalimpsestMatch currentMatch:allPalimpsestMatch
             ) {
            if(date.equals(currentMatch.getDate())){
                result.add(currentMatch);
            }
        }
        if(result.isEmpty()){
            result = null;
        }
        return result;
    }
    
    public ArrayList<PalimpsestMatch> isHourValidate(String hour,ArrayList<PalimpsestMatch> allPalimpsestMatch){
        ArrayList<PalimpsestMatch> result = new ArrayList<>();

        for (PalimpsestMatch currentMatch:allPalimpsestMatch
             ) {
            if(hour.equals(currentMatch.getHour())){
                result.add(currentMatch);
            }
        }
        if(result.isEmpty()){
            result = null;
        }
        return result;
    }
    
    public Map<String,ArrayList<PalimpsestMatch>> isNameValidate(String name,ArrayList<PalimpsestMatch> allPalimpsestMatch){
        ArrayList<PalimpsestMatch> homeTeams = new ArrayList<>();
        ArrayList<PalimpsestMatch> awayTeams = new ArrayList<>();
        Map<String,ArrayList<PalimpsestMatch>> result = new HashMap<>();

        for (PalimpsestMatch currentMatch:allPalimpsestMatch
             ) {
            if(currentMatch.getHomeTeam().getName().contains(name)){
                homeTeams.add(currentMatch);
            }
            else if(currentMatch.getAwayTeam().getName().contains(name)){
                awayTeams.add(currentMatch);
            }
        }

        if(!homeTeams.isEmpty())
            result.put(HOME_TEAM,homeTeams);
        if(!awayTeams.isEmpty())
            result.put(AWAY_TEAM,awayTeams);
        if(result.isEmpty())
            result = null;
        return result;
    }

    public ArrayList<PalimpsestMatch> isHomeNameValidate(String name,ArrayList<PalimpsestMatch> allPalimpsestMatch){
        ArrayList<PalimpsestMatch> result = new ArrayList<>();
        for (PalimpsestMatch currentMatch:allPalimpsestMatch
             ) {
            if(currentMatch.getHomeTeam().getName().contains(name)){
                result.add(currentMatch);
            }
        }
        if(result.isEmpty()){
            return null;
        }
        return result;
    }

    public ArrayList<PalimpsestMatch> isAwayNameValidate(String name,ArrayList<PalimpsestMatch> allPalimpsestMatch){
        ArrayList<PalimpsestMatch> result = new ArrayList<>();
        for (PalimpsestMatch currentMatch:allPalimpsestMatch
                ) {
            if(currentMatch.getAwayTeam().getName().contains(name)){
                result.add(currentMatch);
            }
        }
        if(result.isEmpty()){
            return null;
        }
        return result;
    }
}
