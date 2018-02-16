package com.bettypower.betMatchFinder;

import com.bettypower.betMatchFinder.entities.ConcreteMatchToFind;
import com.bettypower.betMatchFinder.entities.MatchToFind;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.util.Helper;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by giuliopettenuzzo on 21/10/17.
 */

public class RealTimeUpdater {

    private int pointer;
    private ArrayList<MatchToFind> mainListOfMatches;
    private ArrayList<String> mainListOfOdds;
    private ArrayList<PalimpsestMatch> allPalimpsestMatch;
    private Validator validator;
    private Helper helper;


    public RealTimeUpdater(ArrayList<PalimpsestMatch> allPalimpsestMatches){
        validator = new Validator();
        pointer = 0;
        this.allPalimpsestMatch = allPalimpsestMatches;
        mainListOfMatches = new ArrayList<>();
        mainListOfMatches.add(new ConcreteMatchToFind());
        mainListOfMatches.get(pointer).setPalimpsestMatch(allPalimpsestMatch);
        mainListOfOdds = new ArrayList<>();
        this.helper = new Helper();
    }

    public void updateNewElement(Map<String,Object> map){
        //TODO before pass the element to the validator check if mainListOfMatches.get(pointer) has already or not that element
        for (String currentKey:map.keySet()
             ) {
            //TODO la data è un oggetto non una stringa voglio che capisca il formato e poi tenga quello cosi da poterlo trattare come stringa
            if(currentKey.equals(Finder.PALIMPSEST)){
                if(mainListOfMatches.get(pointer).getPalimpsest() != null){
                    updatePointer();
                }
                ArrayList<PalimpsestMatch> allPossibleMatch = validator.isPalimpsestValidate(map.get(currentKey).toString(),mainListOfMatches.get(pointer).getPalimpsestMatch());
                if(allPossibleMatch!=null){
                    mainListOfMatches.get(pointer).setPalimpsest(map.get(currentKey).toString());
                    mainListOfMatches.get(pointer).setPalimpsestMatch(allPossibleMatch);
                }
                else{
                    updatePointer();
                    allPossibleMatch = validator.isPalimpsestValidate(map.get(currentKey).toString(),allPalimpsestMatch);
                    mainListOfMatches.get(pointer).setPalimpsest(map.get(currentKey).toString());
                    mainListOfMatches.get(pointer).setPalimpsestMatch(allPossibleMatch);
                }
            }

            if(currentKey.equals(Finder.DATE)){
                if(mainListOfMatches.get(pointer).getDate() != null){
                    //updatePointer();
                }
                ArrayList<PalimpsestMatch> allPossibleMatch = validator.isDateValidate(map.get(currentKey).toString(),mainListOfMatches.get(pointer).getPalimpsestMatch());
                if(allPossibleMatch!=null){
                    mainListOfMatches.get(pointer).setDate(map.get(currentKey).toString());
                    mainListOfMatches.get(pointer).setPalimpsestMatch(allPossibleMatch);
                }
            }

            if(currentKey.equals(Finder.HOUR)){
                if(mainListOfMatches.get(pointer).getHour() != null){
                    //TODO
                }
                ArrayList<PalimpsestMatch> allPossibleMatch = validator.isHourValidate(map.get(currentKey).toString(),mainListOfMatches.get(pointer).getPalimpsestMatch());
                if(allPossibleMatch!=null){
                    mainListOfMatches.get(pointer).setHour(map.get(currentKey).toString());
                    mainListOfMatches.get(pointer).setPalimpsestMatch(allPossibleMatch);
                }
            }

            if(currentKey.equals(Finder.NAME)){
                resolveNameMethod(map.get(currentKey).toString());
            }

            if(currentKey.equals(Finder.NAME_TWO)){
                resolveNameMethod(map.get(currentKey).toString());
            }

            if(currentKey.equals(Finder.QUOTE)){
             //   if(!validator.isOddsValidate(map.get(currentKey).toString(),mainListOfMatches.get(pointer).getPalimpsestMatch()).isEmpty()){
             //       mainListOfMatches.get(pointer).setOdds(map.get(currentKey).toString());
              //  }
                if(mainListOfMatches.get(pointer).getHour() != null && !mainListOfMatches.get(pointer).getHour().equals(map.get(currentKey).toString()) ||
                        (map.containsKey(Finder.QUOTE) && !map.get(Finder.QUOTE).toString().equals(map.get(currentKey).toString())) ){
                    mainListOfOdds.add(map.get(currentKey).toString());
                }
            }

            if(currentKey.equals(Finder.BOOKMAKER + "NON ANCORA TESTATAO")){
                ArrayList<PalimpsestMatch> result = new ArrayList<>();
                for (PalimpsestMatch currentPalimpsestMatch:allPalimpsestMatch
                     ) {
                /*    if(currentPalimpsestMatch.getBookMaker().equals(map.get(Finder.BOOKMAKER).toString())){
                        result.add(currentPalimpsestMatch);
                    }*/
                }
                allPalimpsestMatch = result;
            }

            if(currentKey.equals(Finder.BET)){
                if(validator.isBetValidate(map.get(currentKey).toString(),mainListOfMatches.get(pointer).getBetKind(),
                        mainListOfMatches.get(pointer).getOdds(),mainListOfMatches.get(pointer).getPalimpsestMatch())){
                    mainListOfMatches.get(pointer).setBet(map.get(currentKey).toString());
                }
                else{
                    mainListOfMatches.get(pointer).setBet(null);
                    mainListOfMatches.get(pointer).setBetKind(null);
                }
            }

            if(currentKey.equals(Finder.BET_KIND)){
                if(validator.isBetKindValidate(mainListOfMatches.get(pointer).getBet(),map.get(currentKey).toString())){
                    mainListOfMatches.get(pointer).setBetKind(map.get(currentKey).toString());
                }
                else {
                    mainListOfMatches.get(pointer).setBet(null);
                    mainListOfMatches.get(pointer).setBetKind(null);
                }
            }
        }
    }

    private void updatePointer(){
        pointer++;
        mainListOfMatches.add(new ConcreteMatchToFind());
        mainListOfMatches.get(pointer).setPalimpsestMatch(allPalimpsestMatch);
    }

    private void resolveNameMethod(String name){
        if(mainListOfMatches.get(pointer).getHomeName()!=null && mainListOfMatches.get(pointer).getAwayName()!=null){
            updatePointer();
        }

        Map<String,ArrayList<PalimpsestMatch>> allPossibleMapMatches = validator.isNameValidate(name,mainListOfMatches.get(pointer).getPalimpsestMatch());
        if(allPossibleMapMatches==null){
            updatePointer();
            resolveNameMethod(name);
        }
        else if(allPossibleMapMatches.containsKey(Validator.HOME_TEAM) && allPossibleMapMatches.containsKey(Validator.AWAY_TEAM)){
            if(mainListOfMatches.get(pointer).getUnknownTeamName()==null){
                if(mainListOfMatches.get(pointer).getHomeName()!=null){
                    mainListOfMatches.get(pointer).setAwayTeamName(name);
                    mainListOfMatches.get(pointer).setPalimpsestMatch(allPossibleMapMatches.get(Validator.AWAY_TEAM));
                }
                else{
                    mainListOfMatches.get(pointer).setUnknownTeamName(name);
                    mainListOfMatches.get(pointer).setPalimpsestMatch(helper.joinTwoPalimpsestLists(allPossibleMapMatches.get(Validator.HOME_TEAM),allPossibleMapMatches.get(Validator.AWAY_TEAM)));
                }
            }
            else{
                mainListOfMatches.get(pointer).setHomeTeamName(mainListOfMatches.get(pointer).getUnknownTeamName());
                mainListOfMatches.get(pointer).setAwayTeamName(name);
                mainListOfMatches.get(pointer).setUnknownTeamName(null);
            }
        }
        else if(allPossibleMapMatches.containsKey(Validator.HOME_TEAM)){
            if(mainListOfMatches.get(pointer).getHomeName()!=null){
                updatePointer();
                resolveNameMethod(name);
            }
            else {
                mainListOfMatches.get(pointer).setHomeTeamName(name);
                mainListOfMatches.get(pointer).setPalimpsestMatch(allPossibleMapMatches.get(Validator.HOME_TEAM));
            }
        }
        else if(allPossibleMapMatches.containsKey(Validator.AWAY_TEAM)){
            if(mainListOfMatches.get(pointer).getAwayName()!=null){
                updatePointer();
                resolveNameMethod(name);
            }
            else {
                mainListOfMatches.get(pointer).setAwayTeamName(name);
                mainListOfMatches.get(pointer).setPalimpsestMatch(allPossibleMapMatches.get(Validator.AWAY_TEAM));
            }
        }
        else{
            //TODO
        }
    }


    public ArrayList<MatchToFind> getMainListOfMatches(){
        return mainListOfMatches;
    }

    public ArrayList<String> getMainListOfOdds(){
        return mainListOfOdds;
    }

}
