package com.bettypower.betMatchFinder;

import com.bettypower.betMatchFinder.labelSet.BetLabelSet;
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
    Map<String, ArrayList<String>> hashBetAndBetKind = new BetLabelSet().hashBetAndBetKind();


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

    //TODO DA QUI IN POI POSSO TOGLIERE
    public Map<String,ArrayList<PalimpsestMatch>> isOddsValidate(String odds,ArrayList<PalimpsestMatch> allPalimpsestMatch){
        Map<String,ArrayList<PalimpsestMatch>> result = new HashMap<>();

        for (PalimpsestMatch currentMatch:allPalimpsestMatch
             ) {
            for (String currentKey:currentMatch.getAllOdds().keySet()) {
                if(odds.equals(currentMatch.getAllOdds().get(currentKey))){
                    //la chiave esiste gia?
                    for (String currentExistingKey:result.keySet()
                         ) {
                        if(currentExistingKey.equals(currentKey)){
                            ArrayList<PalimpsestMatch> currentResult = result.get(currentKey);
                            currentResult.add(currentMatch);
                            result.put(currentKey,currentResult);
                        }
                        else{
                            ArrayList<PalimpsestMatch> currentResult = new ArrayList<>();
                            currentResult.add(currentMatch);
                            result.put(currentKey,currentResult);
                        }
                    }
                }
            }
        }
        
        return result;
    }

    //TODO forse se qui gli passo l'odds to find quando
    public boolean isBetValidate(String bet,String betKind,String quote,ArrayList<PalimpsestMatch> allPalimpsestMatch){
        if(betKind!=null){
            ArrayList<String> allPossibleBet = hashBetAndBetKind.get(betKind);
            if(allPossibleBet!=null) {
                for (String currentBet:allPossibleBet
                    ) {
                    if (currentBet.equals(bet)) {
                        return true;
                    }
                }
            }
        }
        else{
            return true;
        }
       /* if(quote != null){
            //TODO le quote non sono precise quindi le posso controllare ma aggingendo +-delta
            for (PalimpsestMatch currentMatch:allPalimpsestMatch
                    ) {
                for (String currentKey:currentMatch.getAllOdds().keySet()) {
                    if(quote.equals(currentMatch.getAllOdds().get(currentKey))){
                        //la chiave esiste gia?
                        return true;
                    }
                }
            }
        }*/
        return false;
    }

   /* public boolean isBetKindValidate(String bet, String betKind){
        //TODO se il betkind c'è già dovrebbe confrontare i valori e nel caso in cui i valori combaciano sono sicuro del risultato
        //TODO se i valori non combaciano forse andrebbero scartati tutti e due per poi arrivarci guardando la bet
        if(bet!=null){
            ArrayList<String> allPossibleBet = hashBetAndBetKind.get(betKind);
            if(allPossibleBet!=null) {
                for (String currentBet : allPossibleBet
                        ) {
                    if (currentBet.equals(bet)) {
                        return true;
                    }
                }
            }
        }
        else
            return true;
        return false;
    }*/
}
