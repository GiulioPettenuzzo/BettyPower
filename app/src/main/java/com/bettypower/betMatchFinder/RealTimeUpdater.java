package com.bettypower.betMatchFinder;

import com.bettypower.betMatchFinder.betFinderManagment.RealTimeBetUpdater;
import com.bettypower.betMatchFinder.entities.ConcreteMatchToFind;
import com.bettypower.betMatchFinder.entities.ConcreteOddsToFind;
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
    private ArrayList<OddsToFind> mainListOfOdds;
    private ArrayList<PalimpsestMatch> allPalimpsestMatch;
    private Map<String,String> bookMakerAndMoney;
    private Validator validator;
    private Helper helper;
    private RealTimeBetUpdater realTimeBetUpdater;


    public RealTimeUpdater(ArrayList<PalimpsestMatch> allPalimpsestMatches){
        validator = new Validator();
        pointer = 0;
        this.allPalimpsestMatch = allPalimpsestMatches;
        mainListOfMatches = new ArrayList<>();
        mainListOfMatches.add(new ConcreteMatchToFind());
        mainListOfMatches.get(pointer).setPalimpsestMatch(allPalimpsestMatch);
        mainListOfOdds = new ArrayList<>();
        realTimeBetUpdater = new RealTimeBetUpdater();
        bookMakerAndMoney = new HashMap<>();
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
                mainListOfMatches = realTimeBetUpdater.updateNewQuote(map.get(currentKey).toString(),mainListOfMatches);
                //   if(!validator.isOddsValidate(map.get(currentKey).toString(),mainListOfMatches.get(pointer).getPalimpsestMatch()).isEmpty()){
             //       mainListOfMatches.get(pointer).setOdds(map.get(currentKey).toString());
              //  }
             /*   if(mainListOfMatches.get(pointer).getHour() != null && !mainListOfMatches.get(pointer).getHour().equals(map.get(currentKey).toString()) ||
                        (map.containsKey(Finder.QUOTE) && !map.get(Finder.QUOTE).toString().equals(map.get(currentKey).toString())) ){
                    mainListOfOdds.add(new ConcreteOddsToFind());
                    //mainListOfOdds.add(map.get(currentKey).toString());
                }*/
            }

            if(currentKey.equals(Finder.BOOKMAKER)){
                bookMakerAndMoney.put(Finder.BOOKMAKER,map.get(Finder.BOOKMAKER).toString());
                /*ArrayList<PalimpsestMatch> result = new ArrayList<>();
                for (PalimpsestMatch currentPalimpsestMatch:allPalimpsestMatch
                     ) {
                /*    if(currentPalimpsestMatch.getBookMaker().equals(map.get(Finder.BOOKMAKER).toString())){
                        result.add(currentPalimpsestMatch);
                    }*/
               // }
               // allPalimpsestMatch = result;

            }

            if(currentKey.equals(Finder.BET)){

                mainListOfMatches = realTimeBetUpdater.updateNewBet(map.get(currentKey).toString(),mainListOfMatches);
               /* if(validator.isBetValidate(map.get(currentKey).toString(),mainListOfMatches.get(pointer).getBetKind(),
                        mainListOfMatches.get(pointer).getOdds(),mainListOfMatches.get(pointer).getPalimpsestMatch())){
                    mainListOfMatches.get(pointer).setBet(map.get(currentKey).toString());

                    //quando trovo due cose uguali aggiorno il bet to find
                    //TODO se il validator ritorna false aggiorno l'oods to find, se ritorna true carico l'odds to find nel match to find
                }
                else{
                    mainListOfMatches.get(pointer).setBet(null);
                    mainListOfMatches.get(pointer).setBetKind(null);
                }*/
            }

            if(currentKey.equals(Finder.BET_KIND)){
                mainListOfMatches = realTimeBetUpdater.updateNewBetKind(map.get(currentKey).toString(),mainListOfMatches);
                /*
                if(validator.isBetKindValidate(mainListOfMatches.get(pointer).getBet(),map.get(currentKey).toString())){
                    mainListOfMatches.get(pointer).setBetKind(map.get(currentKey).toString());
                }
                else {
                    mainListOfMatches.get(pointer).setBet(null);
                    mainListOfMatches.get(pointer).setBetKind(null);
                }
                */
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


    private int getPointerForBet(){
        int index = 0;
        for (OddsToFind currentOddsToFind:mainListOfOdds
             ) {
            if(currentOddsToFind.getBet()!=null || currentOddsToFind.getBetKind()!=null){
                index++;
            }
        }
        return index;
    }

    private int getPointerForQuote(){
        int index = 0;
        for (OddsToFind currentOddsToFind:mainListOfOdds
                ) {
            if(currentOddsToFind!=null){
                index++;
            }
        }
        return index;
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
