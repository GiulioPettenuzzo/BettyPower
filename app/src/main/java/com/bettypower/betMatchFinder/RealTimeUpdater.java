package com.bettypower.betMatchFinder;

import com.bettypower.betMatchFinder.betFinderManagment.RealTimeBetUpdater;
import com.bettypower.betMatchFinder.entities.ConcreteMatchToFind;
import com.bettypower.betMatchFinder.entities.MatchToFind;
import com.bettypower.betMatchFinder.entities.OddsToFind;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.util.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by giuliopettenuzzo on 21/10/17.
 */

public class RealTimeUpdater {

    private int pointer;
    private ArrayList<MatchToFind> mainListOfMatches;
    private ArrayList<PalimpsestMatch> allPalimpsestMatch;
    private Map<String,String> bookMakerAndMoney;
    private Validator validator;
    private Helper helper;
    private RealTimeBetUpdater realTimeBetUpdater;


    RealTimeUpdater(ArrayList<PalimpsestMatch> allPalimpsestMatches){
        validator = new Validator();
        pointer = 0;
        this.allPalimpsestMatch = allPalimpsestMatches;
        mainListOfMatches = new ArrayList<>();
        mainListOfMatches.add(new ConcreteMatchToFind());
        mainListOfMatches.get(pointer).setPalimpsestMatch(allPalimpsestMatch);
        realTimeBetUpdater = new RealTimeBetUpdater();
        bookMakerAndMoney = new HashMap<>();
        this.helper = new Helper();
    }

    public void updateNewElement(Map<String,Object> map){
        for (String currentKey:map.keySet()
             ) {
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
                //if(mainListOfMatches.get(pointer).getDate() != null){
                    //updatePointer();
                //}
                ArrayList<PalimpsestMatch> allPossibleMatch = validator.isDateValidate(map.get(currentKey).toString(),mainListOfMatches.get(pointer).getPalimpsestMatch());
                if(allPossibleMatch!=null){
                    mainListOfMatches.get(pointer).setDate(map.get(currentKey).toString());
                    mainListOfMatches.get(pointer).setPalimpsestMatch(allPossibleMatch);
                }
            }

            if(currentKey.equals(Finder.HOUR)){
               // if(mainListOfMatches.get(pointer).getHour() != null){
                //}
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
                mainListOfMatches = realTimeBetUpdater.updateNewQuote(map.get(currentKey).toString(),mainListOfMatches);
            }

            if(currentKey.equals(Finder.BOOKMAKER)){
                bookMakerAndMoney.put(Finder.BOOKMAKER,map.get(Finder.BOOKMAKER).toString());
            }

            if(currentKey.equals(Finder.BET)){

                mainListOfMatches = realTimeBetUpdater.updateNewBet(map.get(currentKey).toString(),mainListOfMatches);
            }

            if(currentKey.equals(Finder.BET_KIND)){
                mainListOfMatches = realTimeBetUpdater.updateNewBetKind(map.get(currentKey).toString(),mainListOfMatches);
            }

            if(currentKey.equals(Finder.VINCITA)){
                String currentVincita = bookMakerAndMoney.get(currentKey);
                if(currentVincita==null || isBigger(map.get(currentKey).toString(),currentVincita)){
                    bookMakerAndMoney.put(currentKey,map.get(currentKey).toString());
                }
            }

            if(currentKey.equals(Finder.PUNTATA)){
                bookMakerAndMoney.put(currentKey,map.get(currentKey).toString());
            }

            if(currentKey.equals(Finder.EURO)){
                String currentVincita = bookMakerAndMoney.get(Finder.VINCITA);
                if(currentVincita==null || isBigger(map.get(currentKey).toString(),currentVincita)){
                    bookMakerAndMoney.put(Finder.VINCITA,map.get(currentKey).toString());
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
    }

    private boolean isBigger(String one,String two){
        one = one.replace(",","");
        two = two.replace(",","");
        int numberOne = Integer.parseInt(one);
        int numberTwo = Integer.parseInt(two);
        return numberOne > numberTwo;
    }

    public Map<String,String> getBookMakerMoney(){
        return bookMakerAndMoney;
    }

    public ArrayList<MatchToFind> getMainListOfMatches(){
        return mainListOfMatches;
    }

    public ArrayList<OddsToFind> getMainListOfOdds(){
        return realTimeBetUpdater.getMainListOfOdds();
    }

}
