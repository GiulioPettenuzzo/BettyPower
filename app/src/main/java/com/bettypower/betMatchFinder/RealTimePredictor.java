package com.bettypower.betMatchFinder;

import android.util.Log;

import com.bettypower.betMatchFinder.entities.MatchToFind;
import com.bettypower.betMatchFinder.entities.OddsToFind;
import com.bettypower.betMatchFinder.labelSet.BetLabelSet;
import com.bettypower.entities.PalimpsestMatch;
import com.bettypower.entities.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by giuliopettenuzzo on 21/10/17.
 */

public class RealTimePredictor {

    private ArrayList<PalimpsestMatch> allPalimpsestMatch;
    private RealTimeUpdater realTimeUpdater;
    private String response;
    private ArrayList<String> lastWordsNames;

    //result
    private ArrayList<MatchToFind> elementsFound;
    private ArrayList<OddsToFind> oddsElementFound;
    private Map<String,String> bookmakerAndMoney;


    public RealTimePredictor(ArrayList<PalimpsestMatch> allPalimpsestMatch, String response){
        this.allPalimpsestMatch = allPalimpsestMatch;
        this.response = response;
        realTimeUpdater = new RealTimeUpdater(allPalimpsestMatch);
        lastWordsNames = new ArrayList<>();
    }

    public void elaborateResponsetToFindMatch(){
        StringTokenizer token = new StringTokenizer(response);
        Finder finder = new Finder(allPalimpsestMatch);
        Map<String,Object> allElementFound;
        while(token.hasMoreTokens()){
            String word = token.nextToken();
            finder.setWord(word);
            allElementFound = finder.getWordKinds();
            if(!allElementFound.isEmpty()) {
                allElementFound = realNameFilter(allElementFound);
                realTimeUpdater.updateNewElement(allElementFound);
            }
            for (String currentKey:allElementFound.keySet()
                    ) {
                Log.i("realTime + " + currentKey, allElementFound.get(currentKey).toString());

            }
        }

        elementsFound = realTimeUpdater.getMainListOfMatches();
       // Assembler assembler = new Assembler(elementsFound);
       // elementsFound = assembler.getAllMatchReassembled();

        oddsElementFound = realTimeUpdater.getMainListOfOdds();
        bookmakerAndMoney = realTimeUpdater.getBookMakerMoney();
    }

    /*
     * in order to delete the smaller names that are found before the biggest names
     */
    private Map<String,Object> realNameFilter(Map<String,Object> map){
        boolean isNameTeamFoundNow = false;
        if(map.containsKey(Finder.NAME)){
            String nameFound = map.get(Finder.NAME).toString();
            map.remove(Finder.NAME);
            for(int i = lastWordsNames.size()-1;i>=0;i--){
                if(nameFound.contains(lastWordsNames.get(i))){
                     lastWordsNames.remove(i);
                }
            }
            lastWordsNames.add(nameFound);
            isNameTeamFoundNow = true;
            Log.i("realTime + " ,nameFound);
        }
        if(map.containsKey(Finder.NAME_TWO)){
            String nameFound = map.get(Finder.NAME_TWO).toString();
            map.remove(Finder.NAME_TWO);
            for (String currentName:lastWordsNames
                    ) {
                if(nameFound.contains(currentName)){
                    lastWordsNames.remove(currentName);
                }
            }
            lastWordsNames.add(nameFound);
            isNameTeamFoundNow = true;
            Log.i("realTime + " ,nameFound);
        }
        if(!isNameTeamFoundNow){
            if(lastWordsNames.size()!=0){
                if(lastWordsNames.size() == 1){
                    String nameOne = lastWordsNames.get(0);
                    map.put(Finder.NAME, nameOne);
                    lastWordsNames.clear();
                }
                else if(lastWordsNames.size() == 2){
                    String nameOne = lastWordsNames.get(0);
                    map.put(Finder.NAME, nameOne);

                    String nameTwo = lastWordsNames.get(1);
                    map.put(Finder.NAME_TWO, nameTwo);
                    lastWordsNames.clear();
                }
                else{
                    Log.i("ERRORE","LASTWORDNAMES.SIZE() > 2");
                    lastWordsNames.clear();
                }
            }
        }
        return map;
    }

    public ArrayList<MatchToFind> getAllMatchToFind(){
        return elementsFound;
    }

    public ArrayList<OddsToFind> getAllQuoteTofind(){
        return oddsElementFound;
    }

    public Map<String, String> getBookmakerAndMoney() {
        return bookmakerAndMoney;
    }
}
